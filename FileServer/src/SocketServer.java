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
	private ExecutorService executorService;// 线程池
	private ServerSocket ss = null;
	private int port;// 监听端口
	private boolean quit;// 是否退出
	private Map<Long, FileLog> datas = new HashMap<Long, FileLog>();// 存放断点数据，最好改为数据库存放

	public SocketServer(int port) {
		this.port = port;
		// 初始化线程池
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * 50);
	}

	// 启动服务
	public void start() throws Exception {
		ss = new ServerSocket(port);
		while (!quit) {
			Socket socket = ss.accept();// 接受客户端的请求
			socketList.add(socket);
			// 为支持多用户并发访问，采用线程池管理每一个用户的连接请求
			executorService.execute(new SocketTask(socket));// 启动一个线程来处理请求
		}
	}

	// 退出
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
				// 得到客户端发来的第一行协议数据：Content-Length=143253434;filename=xxx.3gp;sourceid=
				// 如果用户初次上传文件，sourceid的值为空。
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
					// 下面从协议数据中读取各种参数值
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
					if(file.exists()){//如果上传的文件发生重名，然后进行改名
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
					if(position==0) fileOutStream.setLength(Integer.valueOf(filelength));//设置文件长度
					fileOutStream.seek(position);//移动文件指定的位置开始写入数据
					byte[] buffer = new byte[1024];
					int len = -1;
					int length = position;
					while( (len=inStream.read(buffer)) != -1){//从输入流中读取数据写入到文件中
						fileOutStream.write(buffer, 0, len);
						length += len;
						Properties properties = new Properties();
						properties.put("length", String.valueOf(length));
						FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName()+".log"));
						properties.store(logFile, null);//实时记录文件的最后保存位置
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

	// 保存上传记录
	public void save(Long id, File saveFile) {
		// 日后可以改成通过数据库存放
		datas.put(id, new FileLog(id, saveFile.getAbsolutePath()));
	}

	// 当文件上传完毕，删除记录
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