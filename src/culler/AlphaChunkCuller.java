package culler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

public class AlphaChunkCuller {

	static File option = new File("option.txt");
	static File file = new File("WorldChunks");

	static ArrayList<File> filesToBeRead = new ArrayList<File>();
	
	static ArrayList<File> filesToBeCulled = new ArrayList<File>();
	
	public static void main(String[] args) throws FileNotFoundException, IOException { 

		file.mkdirs();		
		System.out.println("Reading world chunks folder");
		//Files in the World CHunks FOlder
		File[] files = file.listFiles();
		
		if(files != null)
		{
			System.out.println("Finding chunk folders");
			for(File chunkFile : files)
			{
				searchFolder(chunkFile);
			}
		}
		
		for(File chunk : filesToBeRead)
		{
			NBTInputStream stream = new NBTInputStream(new DataInputStream(new FileInputStream(chunk)));
			
			CompoundTag tag = (CompoundTag) stream.readTag();
			Map<String, Tag> levelTags = tag.getValue();
			
			CompoundTag tags = (CompoundTag) levelTags.get("Level");
			IntTag xPos = (IntTag) tags.getValue().get("xPos");
			IntTag zPos = (IntTag) tags.getValue().get("zPos");
			
			int chunkX = xPos.getValue();
			int chunkZ = zPos.getValue();
			
			if(chunkToBeCulled(chunkX, chunkZ))
				filesToBeCulled.add(chunk);
				
			stream.close();
		}
		
		
		for(File toBeDeleted : filesToBeCulled)
		{
			Files.deleteIfExists(toBeDeleted.toPath());
		}
		
		System.out.println("Deleted " + filesToBeCulled.size() + " files");
	}
	
	public static void searchFolder(File dir)
	{
		File[] files = dir.listFiles();
		if(files != null)
		{
			for(File file : files)
			{
				if(file.isDirectory())
				{
					searchFolder(file);
				} else if(file.getName().endsWith(".dat"))
				{
					filesToBeRead.add(file);
				}
			}
		}
	}
	
	static int radius = 36;
	
	public static boolean chunkToBeCulled(int xPos, int zPos)
	{
		if(xPos > radius || -radius > xPos)
		{
			return true;
		}
		
		if(zPos > radius || -radius > zPos)
		{
			return true;
		}
		return false;	
	}

}
