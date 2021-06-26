package com.gmail.mattdiamond98.coronacraft.tournament;

import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.nio.file.Path;

public class FileUtil {
public static void save(Object o, File f){
try{
    if(!f.exists()){

        f.createNewFile();

    }
    ObjectOutputStream OutputStream=new ObjectOutputStream(new FileOutputStream(f));
    OutputStream.writeObject(o);
    OutputStream.flush();
    OutputStream.close();

} catch (IOException e) {
    e.printStackTrace();
}

}
public static Object load(File f) {
  try{

      ObjectInputStream InputStream=new ObjectInputStream(new FileInputStream(f));
      Object returnvalue= InputStream.readObject();
      InputStream.close();
      return returnvalue;
  }catch(Exception e){
      return null;
  }


}
public static File clear(File f) {
 String p=f.getPath();
f.delete();
File newf=new File(p);
    try {
        newf.createNewFile();
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
return newf;
}
}
