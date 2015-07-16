package com.geekcap.akka.prime;

import akka.actor.*;
import com.geekcap.akka.prime.message.NumberRangeMessage;

public class PrimeCalculator
{
    public void calculate( long startNumber, long endNumber )
    {
        // Create our ActorSystem, which owns and configures the classes
        ActorSystem actorSystem = ActorSystem.create( "primeCalculator" );

        // Create our listener
        final ActorRef primeListener = actorSystem.actorOf( new Props( PrimeListener.class ), "primeListener" );

        // Create the PrimeMaster: we need to define an UntypedActorFactory so that we can control
        // how PrimeMaster instances are created (pass in the number of workers and listener reference
        ActorRef primeMaster = actorSystem.actorOf( new Props( new UntypedActorFactory() {
            public UntypedActor create() {
                return new PrimeMaster( 15, primeListener );
            }
        }), "primeMaster" );

        // Start the calculation
        primeMaster.tell( new NumberRangeMessage( startNumber, endNumber ) );
    }

    public static void main( String[] args )
    {
        if( args.length < 2 )
        {
            System.out.println( "Usage: java com.geekcap.akka.prime.PrimeCalculator <start-number> <end-number>" );
            System.exit( 0 );
        }

        long startNumber = Long.parseLong( args[ 0 ] );
        long endNumber = Long.parseLong( args[ 1 ] );

        PrimeCalculator primeCalculator = new PrimeCalculator();
        primeCalculator.calculate( startNumber, endNumber );
        //primeCalculator.calculate( 20, 50 );
    }
}