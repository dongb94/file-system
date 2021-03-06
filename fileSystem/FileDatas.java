package fileSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;

/**파일 정보를 담고있는 클래스
 * 생성자를 통해 초기화
 * file[]배열을 이용해서 정보 접근*/
public class FileDatas{
	
	public FileData file[];
	String path;
	Connection conn = DataBase.conn;
	
	/**공백으로 초기화
	 * 엘범, 즐겨찾기 등 이용*/
	FileDatas(){}
	
	/**파일 경로로 초기화*/
	FileDatas(String dir_path){
		this.path = DataBase.Directory_Path_Arrangment(dir_path);
		
		try {
			PreparedStatement pst = conn.prepareStatement("select count(*) from file where path='"+path+"'");
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				int count = rs.getInt(1);
				file = new FileData[count];
			}
			
			pst = conn.prepareStatement("select name,favor,type,album,date,thumnail from file where path='"+path+"' ORDER BY date DESC");
			rs = pst.executeQuery();
			
			int i = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				int fv = rs.getInt(2);
				String type = rs.getString(3);
				int img = rs.getInt(4);
				long date = rs.getTimestamp(5).getTime();
				ImageIcon thumnail=null;
				if(type.equals("img")) thumnail = new ImageIcon(rs.getBytes(6));
				file[i++]=new FileData(path, name, fv, type, img, date, thumnail);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**엘범명으로 초기화*/
	void getAlbumFiles(String album){
		try {
			PreparedStatement pst = conn.prepareStatement("select sid from album where name='"+album+"'");
			ResultSet rs = pst.executeQuery();
			int album_num = 0;
			if(rs.next()){
				album_num = rs.getInt(1);
			}
			
			pst = conn.prepareStatement("select count(*) from file where album='"+album_num+"'");
			rs = pst.executeQuery();
			if(rs.next()){
				int count = rs.getInt(1);
				file = new FileData[count];
			}
			
			pst = conn.prepareStatement("select name,favor,type,path,date,thumnail from file where album='"+album_num+"' ORDER BY date DESC");
			rs = pst.executeQuery();
			
			int i = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				int fv = rs.getInt(2);
				String type = rs.getString(3);
				String path = rs.getString(4);
				long date = rs.getTimestamp(5).getTime();
				ImageIcon thumnail=null;
				if(type.equals("img")) thumnail = new ImageIcon(rs.getBytes(6));
				file[i++]=new FileData(path, name, fv, type, album_num, date, thumnail);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**즐겨찾기로 초기화*/
	void getFavoriteFiles(){
		try {
			PreparedStatement pst = conn.prepareStatement("select count(*) from file where favor=1");
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				int count = rs.getInt(1);
				file = new FileData[count];
			}
			
			pst = conn.prepareStatement("select name,path,type,album,date,thumnail from file where favor=1 ORDER BY date DESC");
			rs = pst.executeQuery();
			
			int i = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				String path = rs.getString(2);
				String type = rs.getString(3);
				int img = rs.getInt(4);
				long date = rs.getTimestamp(5).getTime();
				ImageIcon thumnail=null;
				if(type.equals("img")) thumnail = new ImageIcon(rs.getBytes(6));
				file[i++]=new FileData(path, name, 1, type, img, date, thumnail);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**엘범 명 및 썸네일 가져오기*/
	void getAlbums(){
		try {
			PreparedStatement pst = conn.prepareStatement("select count(*) from album");
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				int count = rs.getInt(1);
				file = new FileData[count-1];
			}

			pst = conn.prepareStatement("select * from album");
			rs = pst.executeQuery();

			int i = 0;
			String name = null;
			int album_num = 0;
			while(rs.next()){
				album_num = rs.getInt(1);
				name = rs.getString(2);

				if(album_num>=0){
					PreparedStatement pstt = conn.prepareStatement("select thumnail from file where album='"+album_num+"'");
					ResultSet rst = pstt.executeQuery();
					ImageIcon thumnail=null;
					if(rst.next()) {
						thumnail = new ImageIcon(rst.getBytes("thumnail"));
					}

					file[i++]=new FileData(name, album_num, thumnail);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**엘범 추가
	 * addAlbum(엘범 이름)*/
	void addAlbum(String name){
		try {
			PreparedStatement pst = DataBase.conn.prepareStatement("insert into album values(?,?)");
			pst.setString(1, null);
			pst.setString(2, name);
			pst.executeUpdate();
		} catch (SQLException e) {
			System.err.println("추가 실패 : 이미 존재하는 엘범");
		}
	}
	/**엘범 삭제
	 * deleteAlbum(엘범 이름)*/
	void deleteAlbum(String name){
		try {
			PreparedStatement pst = DataBase.conn.prepareStatement("delete from album where name ='"+name+"'");
			pst.executeUpdate();
		} catch (SQLException e) {
			System.err.println("삭제 실패");
		}
	}
	
	/**검색기능
	 * find(검색할 문자열)*/
	void find(String s){
		try {
			PreparedStatement pst = conn.prepareStatement("select count(*) from file where name like'%"+s+"%'");
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				int count = rs.getInt(1);
				file = new FileData[count];
			}
			
			pst = conn.prepareStatement("select * from file where name like'%"+s+"%' ORDER BY date DESC");
			rs = pst.executeQuery();
			
			int i = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				String path = rs.getString(2);
				int fv = rs.getInt(3);
				int img = rs.getInt(4);
				long date = rs.getTimestamp(5).getTime();
				String type = rs.getString(6);
				ImageIcon thumnail=null;
				if(type.equals("img")) thumnail = new ImageIcon(rs.getBytes(7));
				file[i++]=new FileData(path, name, fv, type, img, date, thumnail);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**getFileData*/
	FileData[] getFileDatas(){
		return file;
	}
}

class FileData{
	String name;
	String path;
	long date;
	boolean favor=false;
	boolean dir=false;
	boolean vid=false;
	boolean msc=false;
	boolean dcm=false;
	int img;	//-1 = not img, 0 = img without album, 1~ = img in 1~th album
	ImageIcon thumnail;
	Connection conn = DataBase.conn;
	
	FileData(String path,String name){
		this.path = path;
		this.name = name;
		checkFile();
	}
	/**엘범으로 초기화*/
	FileData(String name, int album_num,ImageIcon thumnail){
		this.name =name;
		this.img=album_num;
		this.thumnail=thumnail;
	}
	FileData(String path,String name, int favor, String type, int album, long date, ImageIcon thumnail){
		this.path = path;
		this.name = name;
		img = album;
		this.date = date;
		
		if(favor==1) this.favor=true;
		switch(type){
		case "dir":
			dir=true;
			break;
		case "vid":
			vid=true;
			break;
		case "msc":
			msc=true;
			break;
		case "dcm":
			dcm=true;
			break;
		case "img":
			this.thumnail = thumnail;
		}
	}
	
	private void checkFile(){
		try {
			PreparedStatement pst = conn.prepareStatement("select favor,type,album,date,thumnail from file where name='"+name+"'&&path='"+path+"'");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int fv = rs.getInt(1);
				String type = rs.getString(2);
				img = rs.getInt(3);
				date = rs.getTimestamp(4).getTime();
				
				if(fv==1) favor=true;
				
				switch(type){
				case "dir":
					dir=true;
					break;
				case "vid":
					vid=true;
					break;
				case "msc":
					msc=true;
					break;
				case "dcm":
					dcm=true;
					break;
				case "img":
					thumnail = new ImageIcon(rs.getBytes(5));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**엘범 변경(default에서의 변경 포함)
	 * changeAlbum(변경할 엘범 명)
	 * default로 변경시에는 null로 함*/
	void changeAlbum(String name){
		try {
			int album_num = 0;
			PreparedStatement pst;
			ResultSet rs;
			if(name!=null){
				pst = conn.prepareStatement("select sid from album where name='"+name+"'");
				rs = pst.executeQuery();
				rs.next();
				album_num = rs.getInt(1);
			}
			pst = conn.prepareStatement("update file set album='"+album_num+"' where name='"+this.name+"'&& path='"+this.path+"'&& type='img'");
			pst.executeUpdate();
			
			img = album_num;
			
		} catch (SQLException e) {
			System.err.println("실패 : 존재하지 않는 엘범명이거나 이미 엘범에 포함돼 있습니다.");
		}
	}
	
	/**즐겨찾기 추가*/
	void addFavor(){
		try {
			PreparedStatement pst;
			pst = conn.prepareStatement("update file set favor='1' where name='"+name+"'&& path='"+path+"'");
			pst.executeUpdate();
			
			favor = true;
		} catch (SQLException e) {
			System.err.println("실패 : 이미 즐겨찾기에 포함돼 있습니다.");
		}
	}
	/**즐겨찾기 삭제*/
	void deleteFavor(){
		try {
			PreparedStatement pst;
			pst = conn.prepareStatement("update file set favor='0' where name='"+name+"'&& path='"+path+"'");
			pst.executeUpdate();
			
			favor = false;
		} catch (SQLException e) {
			System.err.println("실패 : .");
		}
	}
}
