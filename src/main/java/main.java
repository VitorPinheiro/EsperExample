import com.espertech.esper.client.*;

import listeners.CEPListener;
import listeners.CEPListener2;
import model.Tick;

import java.util.Random;

/**
 * Tutorials:
 * https://dzone.com/articles/complex-event-processing-made
 *
 *
 */
public class main {

    private static Random generator=new Random();

    public static void GenerateRandomTick(EPRuntime cepRT)
    {
        double price = (double) generator.nextInt(10);
        long timeStamp = System.currentTimeMillis();
        String symbol = "AAPL";
        Tick tick= new Tick(symbol,price,timeStamp);

        System.out.println("------------- New Event -------------");
        System.out.println("Sending tick:" + tick);
        cepRT.sendEvent(tick);
    }

    public static void main(String[] args)
    {
        //SimpleLayout layout = new SimpleLayout();
        //ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());
        //Logger.getRootLogger().addAppender(appender);
        //Logger.getRootLogger().setLevel((Level) Level.WARN);

        //The Configuration is meant only as an initialization-time object.
        Configuration cepConfig = new Configuration();
        cepConfig.addEventType("StockTick", Tick.class.getName());
        EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
        EPRuntime cepRT = cep.getEPRuntime();

        // We register an EPL statement
        EPAdministrator cepAdm = cep.getEPAdministrator();
        EPStatement cepStatement = cepAdm.createEPL("select * from " +
                                                                    "StockTick(symbol='AAPL').win:length(2) " +
                                                                    "having avg(price) > 6.0");

        EPStatement cepStatement2 = cepAdm.createEPL("select avg(price) as avg_val from " +
                "StockTick(symbol='AAPL').win:length(2)");


        cepStatement.addListener(new CEPListener());
        cepStatement2.addListener(new CEPListener2());

        for (int i = 0; i < 5; i++)
        {
            GenerateRandomTick(cepRT);
        }
    }
}