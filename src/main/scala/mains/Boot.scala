package mains

/**
  * Created by alf on 14/09/16.
  */
object Boot extends App{

 args.headOption.foreach{
   case "camera" => BootCameraNode.main(args.tail)
   case "backend" => BootBackNode.main(args.tail)
   case _ => println("option not recongiced")
  }
}
