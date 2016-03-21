import java.io.*;
import oracle.stellent.ridc.*;
import oracle.stellent.ridc.model.*;
import oracle.stellent.ridc.protocol.*;
import oracle.stellent.ridc.protocol.intradoc.*;
import oracle.stellent.ridc.common.log.*;
import oracle.stellent.ridc.model.serialize.*;
import java.util.*;

/*
 * @author Sterin- Oracle Inc
 * 
 * This is a class used to test the basic functionality
 * of submitting a search to Content Server using RIDC.  
 * The response is then used to retrieve metadata about
 * the content items.  
 */

public class FldStructure {

	/**
	 * @param args
	 */

static IdcClient  idcClient;
static IdcContext  userContext;
static DataBinder dataBinder;
static String folderpathsofar="";
static String ParentFolder;
static int contentCounter=0;
static String listofContentsinFolder="";

//static List<String []> finalFolders  = new ArrayList<String[]>(); 

static List<String> finalFolders  = new ArrayList<String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IdcClientManager manager = new IdcClientManager ();

                  	Properties prop = new Properties();
	                InputStream input = null;
                    
               

                    
		try{
			

               input = new FileInputStream("config.properties");
 
		// load a properties file
		prop.load(input);


              // Create a new IdcClient Connection using idc protocol (i.e. socket connection to Content Server)
	   		 idcClient = manager.createClient (prop.getProperty("url"));

                        userContext = new IdcContext (prop.getProperty("user"),prop.getProperty("password"));

			// Create an HdaBinderSerializer; this is not necessary, but it allows us to serialize the request and response data binders
			HdaBinderSerializer serializer = new HdaBinderSerializer ("UTF-8", idcClient.getDataFactory ());
			
			// Create a new binder for submitting a search
			 dataBinder = idcClient.createBinder();

                         String fFolderGUID = args[0];

System.out.println("--> Shows Parent and child folder && <--> Shows the contents ");

                                 folderfind(fFolderGUID);


String newfolderpath="";
String [] tokens2 = new String [2];
String [] tokens = new String [2];
String lastfolder = fFolderGUID;

String [] c1   = new String [finalFolders.size()];
String [] c2   = new String [finalFolders.size()];


System.out.println("......................");
System.out.println("Total Folders Under " + fFolderGUID + "  : " +finalFolders.size());
System.out.println("Total Contents Under " +fFolderGUID + " : " +contentCounter);
System.out.println("Folder Mapping");


                                                             for ( int k=0;k <finalFolders.size(); k++)



                                                     {

                                                                   tokens = finalFolders.get(k).split("\\-->+");
                                                                   c1[k]=tokens[0];
                                                                   c2[k]=tokens[1];
                                                     }


                                                             for ( int k=0;k <finalFolders.size(); k++)

                                                     {

                                                               

                                                                  if ( !IsSubFolder( c1 ,c2[k]))
                                                                         {
                                                                               

                                                                         
                                                                                  if ( c1[k].equals(fFolderGUID))
                                                                                          {

                                                                                             
                                                                                       
                                                                                           System.out.println(c1[k]+"-->"+c2[k]);
                                                                                           }

                                                                                 else {
                                                                                           //   System.out.println(" Sending "+ c1[k]);
                                                                                              getParentFolder(c1[k],c1,c2,fFolderGUID,"");


                                                                                                
                                                                                                   String [] duplicateremover = ParentFolder.split("\\-->+");

                                                                                                  if (duplicateremover[duplicateremover.length-1].equals(c1[k]))
                                                                                                        {
                                                                                                              System.out.println(ParentFolder+"-->"+c2[k]);
                                                                                                        }
                                                                                                 else
                                                                                                     {
                                                                                              System.out.println(ParentFolder+"-->"+c1[k]+"-->"+c2[k]);

                                                                                                      }
                                                                                       }


                                                                        }





                                                     }



           			
		} catch (IdcClientException ice){
			ice.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	} // end of main 



public static List<String> runFLD_RETRIEVE_CHILD_FOLDERS (String fFolderGUID ) throws  IdcClientException

{

//System.out.println("Inside runFLD_RETRIEVE_CHILD_FOLDERS with " + fFolderGUID);



List<String> childFolders  = new ArrayList<String>(); 
listofContentsinFolder="";


                                     //  dataBinder.putLocal("IdcService", "FLD_RETRIEVE_CHILD_FOLDERS");
                                          dataBinder.putLocal("IdcService", "FLD_BROWSE");

                                       dataBinder.putLocal("fFolderGUID",fFolderGUID);         
                                                                         
                           
                        ServiceResponse response = idcClient.sendRequest(userContext,dataBinder);

                         DataBinder responseData = response.getResponseAsBinder();


                             if (responseData.getResultSet("ChildFolders") != null)
                     {               
                     
                               DataResultSet resultSet = responseData.getResultSet("ChildFolders");



                         
                                 for (DataObject dataObject : resultSet.getRows ()) 

                             {
                                           
              //System.out.println("fFolderGUID "+ dataObject.get("fFolderGUID")+" fParentGUID  "+dataObject.get("fParentGUID")   );
                        childFolders.add(dataObject.get("fFolderGUID"));
                      


                            }


                   } // end of folder check if



                                  if (responseData.getResultSet("ChildFiles") != null)
                           {
          
                     
                         DataResultSet   resultSet = responseData.getResultSet("ChildFiles");



                         
                        for (DataObject dataObject : resultSet.getRows ()) 

                           {
                                           
    

                           listofContentsinFolder=dataObject.get("dDocName")+","+listofContentsinFolder;
                           contentCounter++;


                           }


                         } // end of content check if 


return childFolders;

                           

} // end of runFLD_RETRIEVE_CHILD_FOLDERS


public static void folderfind (String    fFolderGUID)   throws  IdcClientException


{
         // System.out.println("Inside folderfind with " + fFolderGUID);

//System.out.println("----------------");


        List<String>    childFolders = runFLD_RETRIEVE_CHILD_FOLDERS(fFolderGUID);

                                        List<String>    subFolders = childFolders;


                                                 if  ( childFolders.size() > 0)
                                                      {
                                                                  //  System.out.println("Inside childFolders "+ subFolders.size());
                                                              // String localfoolder="";

                                                                       // String []arr = new  String [2];
                                                              
                                                           for ( int j=0;j<subFolders.size();j++)
                                                                {
                                                                        
                                                                      

                                                                          System.out.println(fFolderGUID+"-->"+subFolders.get(j)+"<--->"+listofContentsinFolder);  


                                                              

                                                           
                                                                finalFolders.add(fFolderGUID+"-->"+subFolders.get(j));
 
                                                                      
                                                                     folderfind(subFolders.get(j));
                                                                 }
                                                               


                                                      }


                                                 


} // end pf folderfind


public static  boolean  IsSubFolder(String  [] c1,String c2)

{

boolean check=false;

  for ( int i =0,k=0; i < c1.length ; i++)

       {
                             if(c2.equals(c1[i]))
                      {
                              check=true;
                               break;

                       }
    }

//System.out.println("Value of Check "+c2 + " " + check);
return check;

} //end of getIndex


public static  void  getParentFolder (String foldertocheck,String [] c1, String [] c2,String fFolderGUID,String PathSofar )

{

boolean check=false;
ParentFolder="";
int m=0;

//System.out.println(" foldertocheck "+foldertocheck );
                           for (  m=0;m<c1.length;m++)

                               {

                                     if (foldertocheck.equals(c2[m]))
                                        {  check=true; break;                   }


                               }



            if (check)
                 {

                      if ( c1[m].equals(fFolderGUID))
                         {

                         //  System.out.println( c1[m]+"-->"+ foldertocheck+PathSofar);  
                                    
                                   ParentFolder=c1[m]+"-->"+ foldertocheck+PathSofar;
                          }


                      else 


                               {      
                                      PathSofar="-->"+ foldertocheck;  
                                  getParentFolder(c1[m],c1,c2,fFolderGUID,PathSofar);
                               }


                 }


//System.out.println("SS  "+ ParentFolder);

}


} // end of class 
