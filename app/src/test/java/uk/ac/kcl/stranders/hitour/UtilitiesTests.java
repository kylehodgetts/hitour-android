package uk.ac.kcl.stranders.hitour;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stranders.hitour.utilities.Utilities;

public class UtilitiesTests {

    private String complicatedUrl = "http://www.fakeurl.com/h/total%thisisfake:true-1+1=2/helloworld.mp4";

    /**
     * Test the createFilename method returns the expected String
     */
    @Test
    public void testCreateFilename() {
        String testString = Utilities.createFilename(complicatedUrl);
        String answerString = "httpwwwfakeurlcomhtotalthisisfaketrue-1+1=2helloworld.mp4";
        Assert.assertEquals(testString, answerString);
    }

    /**
     * Test the getFileExtension method returns the extension of the file
     */
    @Test
    public void testGetFileExtension() {
        String testExtension = Utilities.getFileExtension(complicatedUrl);
        String answerExtension = "mp4";
        Assert.assertEquals(testExtension, answerExtension);
    }

}
