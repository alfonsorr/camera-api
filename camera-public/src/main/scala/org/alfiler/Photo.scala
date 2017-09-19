package org.alfiler

import java.util.Calendar

import org.alfiler.PhotoFormats.PhotoFormat

case class Photo(date:Calendar, data:Array[Byte], format:PhotoFormat)
case class PhotoPublic(date:Calendar, data:Array[Byte], format:PhotoFormat,
                       room:String, group:String)
