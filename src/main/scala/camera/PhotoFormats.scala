package camera

object PhotoFormats {
  case object JPG extends PhotoFormat {
    def apply(): String = "jpg"
  }

  case object GIF extends PhotoFormat {
    def apply(): String = "gif"
  }

  case object PNG extends PhotoFormat {
    def apply(): String = "png"
  }

  case object BMP extends PhotoFormat {
    def apply(): String = "bmp"
  }
}

sealed trait PhotoFormat {
  def apply(): String
}