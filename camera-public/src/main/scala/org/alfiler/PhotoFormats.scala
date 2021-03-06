package org.alfiler

object PhotoFormats {
  sealed trait PhotoFormat {
    val stringValue: String
  }
  case object JPG extends PhotoFormat {
    val stringValue: String = "jpg"
  }

  case object GIF extends PhotoFormat {
    val stringValue: String = "gif"
  }

  case object PNG extends PhotoFormat {
    val stringValue: String = "png"
  }

  case object BMP extends PhotoFormat {
    val stringValue: String = "bmp"
  }
}
