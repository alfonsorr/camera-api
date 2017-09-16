package mains

object Boot extends App with BootInterface{

 val options = {
   case "org.alfiler.camera" => BootCameraNode.main(args.tail); "org.alfiler.camera"
   case "backend" => BootBackNode.main(args.tail); "backend"
  }

  execute(args)
}


trait BootInterface {

  val options: PartialFunction[String, String]

  def execute(args:Array[String]):Either[String,String] = {
    val f = options.andThen[Either[String,String]](Right(_)).orElse[String,Either[String,String]]{case _:String => Left("option not recogniced")}
    args.headOption.fold[Either[String,String]](Left("first argument is needed"))(f)
  }
}