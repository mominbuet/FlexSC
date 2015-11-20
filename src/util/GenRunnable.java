package util;

import java.util.Arrays;

import org.apache.commons.cli.ParseException;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class GenRunnable<T> extends network.Server implements Runnable {

    Mode m;
    int port;
    protected String[] args;
    public boolean verbose = true;
    public ConfigParser config;

    public void setParameter(ConfigParser config, String[] args) {
        this.m = Mode.getMode(config.getString("Mode"));
        this.port = config.getInt("Port");
        this.args = args;
        this.config = config;
    }

    public void setParameter(Mode m, int port) {
        this.m = m;
        this.port = port;
    }

    public abstract void prepareInput(CompEnv<T> gen) throws Exception;

    public abstract void secureCompute(CompEnv<T> gen) throws Exception;

    public abstract void prepareOutput(CompEnv<T> gen) throws Exception;

    public void run() {
        try {
            if (verbose) {
                System.out.println("connecting GEN");
            }
            listen(port);
            if (verbose) {
                System.out.println("connected");
            }

            @SuppressWarnings("unchecked")
            CompEnv<T> env = CompEnv.getEnv(m, Party.Alice, this);

            double s = System.nanoTime();
            Flag.sw.startTotal();
            prepareInput(env);
            os.flush();
            secureCompute(env);
            os.flush();
            prepareOutput(env);
            os.flush();
            Flag.sw.stopTotal();
            double e = System.nanoTime();
            disconnect();
            if (verbose) {
                System.out.println("Gen running time:" + (e - s) / 1e9);
            }
            System.out.println(env.numOfAnds);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    //@SuppressWarnings("rawtypes")
    public static void main(String[] args) throws ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ConfigParser config = new ConfigParser("Config.conf");
        args = new String[4];
        args[0] = "example.DecryptHE";
        args[1] = new BigInteger( "25014245964971028770597203838370394338474797274846903098368352491545212330531583551869516709265179313783315946776497232942437661233058577960755917810610552839266415657043701045012796503966358348939252019687870098281497653210454152560580974028957386222560459270736241373715815399886015855845217331383378473149")+"";
//        args[1] = new Paillier(true).Encryption(new BigInteger("100")).toString();
//        args[2] = args[1].length() + "";
//        args[1].compareTo(null)
//        args[1] = "10";
//        args[3] = "";
        Class<?> clazz = Class.forName(args[0] + "$Generator");
        GenRunnable run = (GenRunnable) clazz.newInstance();
        run.setParameter(config, Arrays.copyOfRange(args, 1, args.length));
        run.run();
        if (Flag.CountTime) {
            Flag.sw.print();
        }

    }
}
