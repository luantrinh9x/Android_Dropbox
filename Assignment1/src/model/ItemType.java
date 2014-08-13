package model;

import java.util.ArrayList;

public class ItemType 
{
	
	public static ArrayList<Item> Items;
	private static int i = 1;

	public static void LoadModel(ArrayList<String> arrayFolder) {

		Items = new ArrayList<Item>();
		i = 1;
		
		for (String string : arrayFolder) {

			if (string.length() >= 5) {

				if (string.substring(string.length() - 4, string.length())
						.equalsIgnoreCase(".txt")) {
					
					Items.add(new Item(i, "txt.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".pdf")) {
					Items.add(new Item(i, "pdf.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".zip")) {
					Items.add(new Item(i, "zip.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".rar")) {
					Items.add(new Item(i, "rar.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".mp3")||string.substring(string.length() - 4,
								string.length()).equalsIgnoreCase(".m4a")) {
					Items.add(new Item(i, "music.png", string));
				} else if (string.substring(string.length() - 5,
						string.length()).equalsIgnoreCase(".pptx")) {
					Items.add(new Item(i, "pptx.png", string));
				} else if (string.substring(string.length() - 5,
						string.length()).equalsIgnoreCase(".docx")||string.substring(string.length() - 4,
								string.length()).equalsIgnoreCase(".doc")) {
					Items.add(new Item(i, "docx.png", string));
				} else if (string.substring(string.length() - 3,
						string.length()).equalsIgnoreCase(".7z")) {
					Items.add(new Item(i, "7z.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".jar")) {
					Items.add(new Item(i, "jar.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".xml")) {
					Items.add(new Item(i, "xml.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".css")) {
					Items.add(new Item(i, "css.png", string));
				} else if (string.substring(string.length() - 3,
						string.length()).equalsIgnoreCase(".js")) {
					Items.add(new Item(i, "js.png", string));
				} else if (string.substring(string.length() - 5,
						string.length()).equalsIgnoreCase(".aspx")) {
					Items.add(new Item(i, "aspx.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".sql")) {
					Items.add(new Item(i, "sql.png", string));
				} else if (string.substring(string.length() - 5,
						string.length()).equalsIgnoreCase(".xlsx")) {
					Items.add(new Item(i, "xlsx.png", string));
				} else if (string.substring(string.length() - 4,
						string.length()).equalsIgnoreCase(".png")||string.substring(string.length() - 4,
								string.length()).equalsIgnoreCase(".jpg")) {
					Items.add(new Item(i, "pic.png", string));
				} else if (string.substring(string.length() - 5,
						string.length()).equalsIgnoreCase(".html")||string.substring(string.length() - 4,
								string.length()).equalsIgnoreCase(".htm")) {
					//
					Items.add(new Item(i, "html.png", string));
					//
				} else {
					Items.add(new Item(i, "folder.png", string));
				}

			} else {
				Items.add(new Item(i, "folder.png", string));
			}
			i++;
		}
	}

	public static Item GetbyId(int id) {

		for (Item item : Items) {
			if (item.Id == id) {
				return item;
			}
		}
		return null;
	}

}
