package com.tumblr

import java.io.File

import org.imgscalr.Scalr
import org.imgscalr.Scalr.Method
import org.imgscalr.Scalr.Mode

import com.madgag.gif.fmsware.AnimatedGifEncoder
import com.madgag.gif.fmsware.GifDecoder

object Main {
  def main(args: Array[String]) {
    if (args.length < 1) {
      System.err.println("Source images file must be specified")
      System.exit(-1)
    }

    if (args.length < 2) {
      System.err.println("Destination image file must be specified")
      System.exit(-1)
    }

    val sourceFile = new File(args(0))
    if (!sourceFile.exists()) {
      System.err.println("Source image not found")
      System.exit(-1)
    }

    val destFile = new File(args(1))
    if (destFile.exists()) {
      println("Destination file exists, overwriting")
      destFile.delete()
    }

    resize(sourceFile, destFile)

  }

  def resize(sourceFile: File, destFile: File) {
    val start = System.currentTimeMillis()
    // create a gif decoder
    val gifDecoder = new GifDecoder()
    gifDecoder.read(sourceFile.getAbsolutePath())

    val frames = for (val frame <- 0 to gifDecoder.getFrameCount() - 1) yield {
      gifDecoder.getFrame(frame)
    }

    val loopCount = gifDecoder.getLoopCount()

    val gifEncoder = new AnimatedGifEncoder()
    gifEncoder.start(destFile.getAbsolutePath())
    gifEncoder.setRepeat(loopCount)
    frames.foldLeft(gifEncoder)((gifEncoder, frame) => {
      val image = frame.getFrameImage()
      val resized = Scalr.resize(image, Method.SPEED, Mode.AUTOMATIC, image.getWidth() / 2, image.getHeight() / 2)
      gifEncoder.setDelay(frame.getDelay())
      gifEncoder.addFrame(resized)
      gifEncoder.setDispose(frame.getDispose())
      gifEncoder
    }).finish()
    val end = System.currentTimeMillis()
    println("Done resizing in ms " + (end - start))
  }
}