package org.jmc.entities.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmc.entities.models.OBJInputFile.OBJGroup;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.util.Log;
import org.jmc.util.Resources;

//TODO: this class is a copy of org.jmc.models.Mesh class
//should figure out how to merge the two...

@SuppressWarnings("unused")
public class Mesh extends EntityModel
{
	
	private static Map<String,OBJInputFile> files=null;
	
	private class MeshObject
	{		
		byte data;
		byte mask;
		
		Transform transform;
		
		OBJInputFile file;
		OBJGroup group;		
	}
	
	private List<MeshObject> objects;
	
	public Mesh()
	{
		if(files==null) files=new HashMap<String, OBJInputFile>();
		objects=new LinkedList<MeshObject>();
	}
	
	public void addMesh(String objectstr)
	{
		addMesh(objectstr,(byte)-1,(byte)0,null);
	}
	
	public void addMesh(String objectstr, byte data, byte mask, Transform transform)
	{
		String [] tok=objectstr.trim().split("#");
		String filename=tok[0];
				
		OBJInputFile objin=null;
				
		if(files.containsKey(filename))
			objin=files.get(filename);
		else
		{
			objin=new OBJInputFile();
			String filePath = "/conf/" + filename;

			try {
				objin.loadFile(filePath, Resources.load(filePath));
			} catch (IOException e) {
				Log.error("Cannot load mesh file!", e);
				return;
			}
			
			files.put(filename, objin);
		}
		
		OBJGroup group=null;
		
		if(tok.length>1)
			group=objin.getObject(tok[1]);
		else
			group=objin.getDefaultObject();
			
		if(group==null)
		{
			Log.info("Cannot find "+objectstr+" object!");
			return;
		}
		
		MeshObject object=new MeshObject();
		object.group=group;
		object.file=objin;		
		object.mask=mask;
		object.data=data;
		object.transform=transform;
		
		objects.add(object);
		
	}

	@Override
	public void addEntity(ChunkProcessor obj, Transform transform) 
	{
		if(objects.size()==0) return;
		MeshObject object=objects.get(0);
		object.file.addObjectToOutput(object.group, transform, obj);
	}

}
