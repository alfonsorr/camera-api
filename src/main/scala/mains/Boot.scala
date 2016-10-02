package mains

/**
  * Created by alf on 14/09/16.
  */
object Boot extends App{

  if (args.headOption.contains("camera")) {
    BootCameraNode.main(args.tail)
  } else {
    BootBackNode.main(args.tail)
  }
}
