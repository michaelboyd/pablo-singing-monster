package com.monster.service;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.monster.domain.Monster;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.image.utils.ImageSize;
import com.sun.media.jai.codec.SeekableStream;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
@Service
public class PictureService {
	
	/**
	 * the following is from
	 * http://www.digitalsanctuary.com/tech-blog/java/how-to-resize-uploaded-images-using-java-better-way.html 
	 */
    private static final String JAI_STREAM_ACTION = "stream";
    private static final String JAI_SUBSAMPLE_AVERAGE_ACTION = "SubsampleAverage";
    private static final String JAI_ENCODE_FORMAT_JPEG = "JPEG";
    private static final String JAI_ENCODE_ACTION = "encode";
    //private static final String JPEG_CONTENT_TYPE = "image/jpeg";
    
    /**
    * this gets rid of exception for not using native acceleration
    */
    static
    {
    	System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    } 	
    
	@Value("${image_full_max_width}")
	private static int maxWidthFull;
	@Value("${image_big_max_width}")
    private static int maxWidthBig;
	@Value("${image_thumb_max_width}")
	private static int maxWidthThumb;	
	
	public void saveImage(Monster monster, byte[] file, PictureRepository pictureRepo) {	
	
		try
		{
			//full size file
			Picture fullSizeFile = new Picture();
			fullSizeFile.setCreateDate(new Date());
			fullSizeFile.setFile(resizeImageAsJPG(file, maxWidthFull));
			fullSizeFile.setImageSize(ImageSize.fullSize);
			fullSizeFile.setMonster(monster);
			pictureRepo.save(fullSizeFile); 
			
			//big file
			Picture bigFile = new Picture();
			bigFile.setCreateDate(new Date());
			bigFile.setFile(resizeImageAsJPG(file, maxWidthBig));
			bigFile.setImageSize(ImageSize.big);
			bigFile.setMonster(monster);
			pictureRepo.save(bigFile);
			
			//thumb file
			Picture thumbFile = new Picture();
			thumbFile.setCreateDate(new Date());
			thumbFile.setFile(resizeImageAsJPG(file, maxWidthThumb));
			thumbFile.setImageSize(ImageSize.thumb);
			thumbFile.setMonster(monster);
			pictureRepo.save(thumbFile);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		
	}
	
    /**
     * This method takes in an image as a byte array (currently supports GIF, JPG, PNG and
     * possibly other formats) and
     * resizes it to have a width no greater than the pMaxWidth parameter in pixels.
     * It converts the image to a standard
     * quality JPG and returns the byte array of that JPG image.
     *
     * @param pImageData
     *                the image data.
     * @param pMaxWidth
     *                the max width in pixels, 0 means do not scale.
     * @return the resized JPG image.
     * @throws IOException
     *                 if the image could not be manipulated correctly.
     */
	public static byte[] resizeImageAsJPG(byte[] pImageData, int pMaxWidth) {
		InputStream imageInputStream = new ByteArrayInputStream(pImageData);
		
		// read in the original image from an input stream
		SeekableStream seekableImageStream = SeekableStream.wrapInputStream(imageInputStream, true);
		RenderedOp originalImage = JAI.create(JAI_STREAM_ACTION, seekableImageStream);
		((OpImage) originalImage.getRendering()).setTileCache(null);
		int origImageWidth = originalImage.getWidth();
		// now resize the image
		double scale = 1.0;
		if (pMaxWidth > 0 && origImageWidth > pMaxWidth) {
			scale = (double) pMaxWidth / originalImage.getWidth();
		}
		ParameterBlock paramBlock = new ParameterBlock();
		paramBlock.addSource(originalImage); // The source image
		paramBlock.add(scale); // The xScale
		paramBlock.add(scale); // The yScale
		paramBlock.add(0.0); // The x translation
		paramBlock.add(0.0); // The y translation

		RenderingHints qualityHints = new RenderingHints(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		RenderedOp resizedImage = JAI.create(JAI_SUBSAMPLE_AVERAGE_ACTION,
				paramBlock, qualityHints);

		// lastly, write the newly-resized image to an output stream, in a
		// specific encoding
		ByteArrayOutputStream encoderOutputStream = new ByteArrayOutputStream();
		JAI.create(JAI_ENCODE_ACTION, resizedImage, encoderOutputStream,
				JAI_ENCODE_FORMAT_JPEG, null);
		// Export to Byte Array
		byte[] resizedImageByteArray = encoderOutputStream.toByteArray();
		return resizedImageByteArray;
	}	

}
