import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
	public static ArrayList<Socket> socketList = new ArrayList<Socket>();
	private String uploadPath="D:/uploadFile/";
	private ExecutorService executorService;// �̳߳�
	private ServerSocket ss = null;
	private int port;// �����˿�
	private boolean quit;// �Ƿ��˳�
	private Map<Long, FileLog> datas = new HashMap<Long, FileLog>();// ��Ŷϵ����ݣ���ø�Ϊ���ݿ���

	public SocketServer(int port) {
		this.port = port;
		// ��ʼ���̳߳�
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * 50);
	}

	// ��������
	public void start() throws Exception {
		ss = new ServerSocket(port);
		while (!quit) {
			Socket socket = ss.accept();// ���ܿͻ��˵�����
			socketList.add(socket);
			// Ϊ֧�ֶ��û��������ʣ������̳߳ع���ÿһ���û�����������
			executorService.execute(new SocketTask(socket));// ����һ���߳�����������
		}
	}

	// �˳�
	public void quit() {
		this.quit = true;
		try {
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		SocketServer server = new SocketServer(9000);
		server.start();
	}

	private class SocketTask implements Runnable {
		private Socket socket;

		public SocketTask(Socket socket) {
			this.socket = socket;
		}

		
		public void run() {
			try {
				System.out.println("accepted connenction from "
						+ socket.getInetAddress() + " @ " + socket.getPort());
				PushbackInputStream inStream = new PushbackInputStream(
						socket.getInputStream());
				// �õ��ͻ��˷����ĵ�һ��Э�����ݣ�Content-Length=143253434;filename=xxx.3gp;sourceid=
				// ����û������ϴ��ļ���sourceid��ֵΪ�ա�
				String head = StreamTool.readLine(inStream);
				System.out.println(head);
				
				if ("Plain".equals(head)) {
					//System.out.println("here");
					try{
						//BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
						String contentString = StreamTool.readLine(inStream);
						System.out.println(contentString);
						while(contentString!=null){
							for(Iterator<Socket> it=socketList.iterator();it.hasNext();){
								Socket s=it.next();
								if(s==socket)
									continue;
								//Socket socket=it.next();
								try{
									//System.out.println(contentString);
									OutputStream os=s.getOutputStream();
									if("Plain".equals(contentString)){
										
									}
									else {
										os.write((contentString+"\n").getBytes("utf-8"));
									}
									
								}
								catch(SocketException e){
									e.printStackTrace();
									it.remove();
									//System.out.println(socketList);
								}
							}
							contentString=StreamTool.readLine(inStream);
						}
						
						
					}
					catch(IOException e){
						e.printStackTrace();
					}
				}
				else{
					if(head!=null){
					// �����Э�������ж�ȡ���ֲ���ֵ
					String[] items = head.split(";");
					String filelength = items[0].substring(items[0].indexOf("=") + 1);
					String filename = items[1].substring(items[1].indexOf("=") + 1);
					Long id = System.currentTimeMillis();
					FileLog log = null;
					File file = null;
					int position = 0;
					String path = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date());
					File dir = new File(uploadPath+ path);
					if(!dir.exists()) dir.mkdirs();
					file = new File(dir, filename);
					if(file.exists()){//����ϴ����ļ�����������Ȼ����и���
						filename = filename.substring(0, filename.indexOf(".")-1)+ dir.listFiles().length+ filename.substring(filename.indexOf("."));
						file = new File(dir, filename);
					}
					save(id, file);

					for(Iterator<Socket> it=socketList.iterator();it.hasNext();){
						Socket s=it.next();
						if(s==socket)
							continue;
						//Socket socket=it.next();
						try{
							//System.out.println(contentString);
							OutputStream os=s.getOutputStream();
							os.write("Voice\n".getBytes("utf-8"));
						}
						catch(SocketException e){
							e.printStackTrace();
							it.remove();
							//System.out.println(socketList);
						}
					}
					
					RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
					if(position==0) fileOutStream.setLength(Integer.valueOf(filelength));//�����ļ�����
					fileOutStream.seek(position);//�ƶ��ļ�ָ����λ�ÿ�ʼд������
					byte[] buffer = new byte[1024];
					int len = -1;
					int length = position;
					while( (len=inStream.read(buffer)) != -1){//���������ж�ȡ����д�뵽�ļ���
						fileOutStream.write(buffer, 0, len);
						length += len;
						Properties properties = new Properties();
						properties.put("length", String.valueOf(length));
						FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName()+".log"));
						properties.store(logFile, null);//ʵʱ��¼�ļ�����󱣴�λ��
						logFile.close();
					}
					if(length==fileOutStream.length()) delete(id);
					fileOutStream.close();					
					inStream.close();
					file = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
	                if(socket != null && !socket.isClosed()) socket.close();
	            } catch (IOException e) {}
			}
		}

	}

	public FileLog find(Long sourceid) {
		return datas.get(sourceid);
	}

	// �����ϴ���¼
	public void save(Long id, File saveFile) {
		// �պ���Ըĳ�ͨ�����ݿ���
		datas.put(id, new FileLog(id, saveFile.getAbsolutePath()));
	}

	// ���ļ��ϴ���ϣ�ɾ����¼
	public void delete(long sourceid) {
		if (datas.containsKey(sourceid))
			datas.remove(sourceid);
	}

	private class FileLog {
		private Long id;
		private String path;
		
		public FileLog(Long id, String path) {
			super();
			this.id = id;
			this.path = path;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}
}