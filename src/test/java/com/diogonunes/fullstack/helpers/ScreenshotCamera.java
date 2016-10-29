package com.diogonunes.fullstack.helpers;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ScreenshotCamera {

    private final WebDriver driver;
    private final String folderPath;

    public ScreenshotCamera(WebDriver driver, String screenshotsFolderPath) {
        this.driver = driver;
        this.folderPath = screenshotsFolderPath;
    }

    public void capture(String screenshotName) {
        capture(driver, folderPath, screenshotName);
    }

    public static void capture(WebDriver driver, String screenshotFolder, String screenshotName) {
        if (driver == null) {
            System.out.println("Can't take a screenshot because web driver is null.");
            return;
        }
        try {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshotFile, new File(screenshotFolder + screenshotName + ".png"));
        } catch (Exception e) {
            String[] errMessages = e.getMessage().split("\\n");
            System.out.println("Exception while taking a screenshot. Cause: " + errMessages[0]);
        }
    }
}
