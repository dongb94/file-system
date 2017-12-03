package fileSystem;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class MenuItem extends JPanel {

	private double width;
	private double height;
	private JButton upload;
	private JButton download;
	private JButton delete;
	private JButton search;
	private JButton star;
	private JTextField search_name;
	private JButton plus;
	private static JLabel show_path;

	private static String current_path = "/";
	private String file_name = "file";
	private String folder_name = "folder";

	private int btn_size;
	
	private static ArrayList<String> file_name_list;
	private static InnerPane ip;
	private static FileData[] fd;
	private static FileDatas fds;
	private static Bookmark bm;
	
	FTPManager fm;

	MenuItem(FTPManager fm) {
		this.fm = fm;
		setLayout(null);

		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		width = res.width * 0.8;
		height = res.height * 0.8 * 0.1;

		btn_size = (int) (height * 0.9);
		this.setBackground(Color.yellow);
		
		Font f = new Font("휴먼매직체", Font.BOLD, 20);

		search_name = new JTextField();
		search = new JButton("찾기");
		upload = new JButton("업");
		download = new JButton("다운");
		delete = new JButton("삭제");
		
		show_path = new JLabel("current path : " +current_path);
		show_path.setFont(f);

		ImageIcon plus_icon = new ImageIcon("img/nfolder.png");
		Image plus_img = plus_icon.getImage();
		plus_img = plus_img.getScaledInstance(btn_size, btn_size, java.awt.Image.SCALE_SMOOTH);
		plus_icon = new ImageIcon(plus_img);
		plus = new JButton(plus_icon);
		
		ImageIcon star_icon = new ImageIcon("img/star.png");
		Image star_img = star_icon.getImage();
		star_img = star_img.getScaledInstance(btn_size, btn_size, java.awt.Image.SCALE_SMOOTH);
		star_icon = new ImageIcon(star_img);
		star = new JButton(star_icon);
		
		ImageIcon sch_icon = new ImageIcon("img/sch.png");
		Image sch_img = sch_icon.getImage();
		sch_img = sch_img.getScaledInstance(btn_size, btn_size, java.awt.Image.SCALE_SMOOTH);
		sch_icon = new ImageIcon(sch_img);
		search = new JButton(sch_icon);
		
		ImageIcon up_icon = new ImageIcon("img/up.png");
		Image up_img = up_icon.getImage();
		up_img = up_img.getScaledInstance(btn_size, btn_size, java.awt.Image.SCALE_SMOOTH);
		up_icon = new ImageIcon(up_img);
		upload = new JButton(up_icon);
		
		ImageIcon down_icon = new ImageIcon("img/down.png");
		Image down_img = down_icon.getImage();
		down_img = down_img.getScaledInstance(btn_size, btn_size, java.awt.Image.SCALE_SMOOTH);
		down_icon = new ImageIcon(down_img);
		download = new JButton(down_icon);
		
		ImageIcon delete_icon = new ImageIcon("img/delete.png");
		Image delete_img = delete_icon.getImage();
		delete_img = delete_img.getScaledInstance(btn_size, btn_size, java.awt.Image.SCALE_SMOOTH);
		delete_icon = new ImageIcon(delete_img);
		delete = new JButton(delete_icon);

		add(search_name);
		add(search);
		add(upload);
		add(download);
		add(delete);
		add(plus);
		add(show_path);
		add(star);
		
		star.setToolTipText("즐겨찾기에 등록합니다.");
		search.setToolTipText("파일 이름을 검색합니다.");
		upload.setToolTipText("사용자의 컴퓨터에서 파일을 업로드 합니다.");
		download.setToolTipText("선택한 파일을 사용자의 컴퓨터 저장소로 다운로드합니다.");
		delete.setToolTipText("선택한 파일을 삭제합니다.");
		plus.setToolTipText("현재 위치에 폴더를 추가합니다.");
		
		
		show_path.setBounds((int)(width*0.01), (int)(height * 0.05), (int)(width*2/5), btn_size);
		delete.setBounds((int) (width - height * 1.20), (int) (height * 0.05), btn_size, btn_size);
		download.setBounds((int) (width - height * 2.20), (int) (height * 0.05), btn_size, btn_size);
		upload.setBounds((int) (width - height * 3.20), (int) (height * 0.05), btn_size, btn_size);
		search.setBounds((int) (width - height * 4.20), (int) (height * 0.05), btn_size, btn_size);
		star.setBounds((int) (width - height * 10.60), (int) (height * 0.05), btn_size, btn_size);
		search_name.setBounds((int) (width - height * 8.40), (int) (height * 0.3), (int) (height * 4),
				(int) (height * 0.40));
		plus.setBounds((int) (width - height * 9.60), (int) (height * 0.05), btn_size, btn_size);

		upload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int ret = chooser.showOpenDialog(null);
				if (ret != JFileChooser.APPROVE_OPTION)
					return;
				String filePath = chooser.getSelectedFile().getAbsolutePath();
				filePath = filePath.replaceAll("\\\\", "/");
				System.out.println(filePath);
				System.out.println(filePath);
				fm.FTPUpload(current_path, filePath);
				ip.reload();
			}
		});

		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int ret = chooser.showSaveDialog(null);
				if (ret != JFileChooser.APPROVE_OPTION)
					return;
				String filePath = chooser.getSelectedFile().getAbsolutePath();
				while(!file_name_list.isEmpty()) {
					int i= 0;
					fm.FTPDownload(filePath, file_name_list.get(i));
					file_name_list.remove(i++);
				}
				

			}

		});
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				while(!file_name_list.isEmpty()) {
					int i= 0;
					System.out.println(current_path);
					if(current_path=="/") {
					fm.FTPDelete(current_path + file_name_list.get(i));
					}
					else {
					fm.FTPDelete(current_path + "/" + file_name_list.get(i));
					}
					file_name_list.remove(i++);
				}
				bm.reload();
				ip.reload();
			}
		});
		plus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				folder_name = JOptionPane.showInputDialog("추가할 폴더 이름을 입력하세요");
				fm.FTPMkdir(folder_name);
				ip.reload();
			}

		});
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = search_name.getText();
				fds.find(name);
				fd = fds.getFileDatas();
				ip.set_fd(fd);
				search_name.setText("");
			}
		});
		star.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while(!file_name_list.isEmpty()) {
					int i = 0;
					for(int j=0; j<fd.length; j++) {
						String temp_name = fd[j].name;
						System.out.println("original : "+ fd[j].name);
						if(fd[j].name.indexOf("/")!= -1)
							temp_name = fd[j].name.replaceAll("/", "");
						else if (fd[j].name.indexOf("\\") != -1)
							temp_name = fd[j].name.replaceAll("\\\\", "");
						else
							;
						System.out.println("equals : "+temp_name);
						if (file_name_list.get(i).equals(temp_name)) {
							if(!fd[j].favor) {
								fd[j].addFavor();
								System.out.println("add favor!!");
							}
							else {
								fd[j].deleteFavor();
								System.out.println("delete favor!!");
							}
							System.out.println("favor : " +fd[j].favor);
						}
					}
					file_name_list.remove(i);
					i++;
				}
				bm.reload();
				ip.reload();
			}
		});

		setVisible(true);
	}

	public static String get_path() {
		return current_path;
	}

	public static void set_path(String path) {
		current_path = path;
		show_path.setText("current path : "+path);
	}
	public static void set_file_list(ArrayList<String> list) {
		file_name_list = list;
	}
	public static void set_ip(InnerPane inp) {
		ip = inp;
	}
	public static void set_fd(FileData[] data) {
		fd = data;
	}
	public static void set_fds(FileDatas file_datas) {
		fds = file_datas;
	}
	public static void set_bmk(Bookmark bk) {
		bm = bk;
	}
}
