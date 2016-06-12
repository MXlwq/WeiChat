package liwenquan.top.weichat;

import java.io.Serializable;

 

// ����ʵ��Serializable�ӿڡ������޷�����ObjectOutputStream��

// writeObject����������ObjectInputStream�е�readObject����

public class FilePojo implements Serializable

{

         private static final long serialVersionUID = 1L;

        

         private String fileName;            // �ļ�����

         private long fileLength;             // �ļ�����

         private byte[] fileContent;          // �ļ�����

        

         public String getFileName()

         {

                   return fileName;

         }

        

         public void setFileName(String fileName)

         {

                   this.fileName = fileName;

            }

        

            public long getFileLength()

         {

                   return fileLength;

         }

        

         public void setFileLength(long fileLength)

         {

                   this.fileLength = fileLength;

         }

        

         public byte[] getFileContent()

         {

                   return fileContent;

         }

        

         public void setFileContent(byte[] fileContent)

         {

                   this.fileContent = fileContent;

            }

}