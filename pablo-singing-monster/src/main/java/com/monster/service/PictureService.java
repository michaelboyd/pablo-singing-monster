package com.monster.service;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.monster.domain.Island;
import com.monster.domain.Monster;
import com.monster.domain.Picture;
import com.monster.domain.PictureRepository;
import com.monster.utils.ImageSize;
import com.sun.media.jai.codec.SeekableStream;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
public class PictureService {
	
	@Autowired
	public PictureRepository pictureRepo;	
	
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
    
	private int maxWidthFull;
    private int maxWidthBig;
	private int maxWidthThumb;	
	Hashtable sizeEnumToWidthMapping = new Hashtable<Integer, ImageSize>();
	
	@Autowired
	public PictureService(@Value("${image_full_max_width}") int maxWidthFull,
			@Value("${image_big_max_width}") int maxWidthBig,
			@Value("${image_thumb_max_width}") int maxWidthThumb) {
		this.maxWidthFull = maxWidthFull;
		this.maxWidthBig = maxWidthBig;
		this.maxWidthThumb = maxWidthThumb;
		sizeEnumToWidthMapping.put(ImageSize.big, maxWidthBig);
		sizeEnumToWidthMapping.put(ImageSize.fullSize, maxWidthFull);
		sizeEnumToWidthMapping.put(ImageSize.thumb, maxWidthThumb);
	}
	
	public void savePicture(Object entity, byte[] fileData, String fileName) {	
		
		Picture picture = null;
		ImageSize[] sizes = ImageSize.values();
		for(int i = 0; i<sizes.length; i++) {

			picture = new Picture();
			//dynamically associate entity
			if(entity instanceof Monster) {
				picture.setMonster((Monster) entity);
			}
			else if(entity instanceof Island) {
				picture.setIsland((Island) entity);
			}
			picture.setImageSize(sizes[i]);
			picture.setCreateDate(new Date());
			picture.setFile(resizeImageAsJPG(fileData, (int)sizeEnumToWidthMapping.get(sizes[i])));
			picture.setFileName(fileName);
			pictureRepo.save(picture); 	
			
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
