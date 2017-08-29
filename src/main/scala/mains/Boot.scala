package mains

object Boot extends App{

 args.headOption.foreach{
   case "camera" => BootCameraNode.main(args.tail)
   case "backend" => BootBackNode.main(args.tail)
   case _ => println("option not recongiced")
  }
}
