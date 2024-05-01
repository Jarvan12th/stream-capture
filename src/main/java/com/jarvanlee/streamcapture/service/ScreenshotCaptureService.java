package com.jarvanlee.streamcapture.service;

import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotCaptureService {
    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {
        String streamURL = "rtmp://192.168.1.13:1935/live";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamURL);
        grabber.start();

        Frame capturedFrame = grabber.grabImage();
        int frameIndex = 0;
        long nextScreenshotTime = 0;

        while (capturedFrame != null) {
            if (capturedFrame.timestamp >= nextScreenshotTime) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(capturedFrame);
                File outputfile = new File("screenshot" + frameIndex++ + ".png");
                try {
                    ImageIO.write(bufferedImage, "png", outputfile);
                    System.out.println("Screenshot taken: " + outputfile.getPath());
                    nextScreenshotTime += 3000000; // Schedule next screenshot after 3 seconds
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            grabber.flush();
            grabber.stop();
            Thread.sleep(3000);
            grabber.start();
            capturedFrame = grabber.grabImage();
        }

        grabber.stop();
    }
}
