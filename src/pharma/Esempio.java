/**
* @author Giuliana Mazzi
* @version 1.0 del 9 luglio 2013
*/
package pharma;

public class Esempio {
	public O_Magazzino magazzinoCentrale = null;
	
	public Esempio(){
		String[] id = {"ASC","ASU","BBC","DSG"};
		String[] nome = {"Tachipirina","Tachipirina","Plasil","DropStar"};
		String[] eccipiente = {"AB","DF","PO","AI"};
		String[] produttore = {"GSK","GSK","DSS","GHR"};
		String[] formato = {"sciroppo","supposte","compresse","bustine"};
		Integer[] quantita = {20,5,7,30};
		O_Prodotto[] prodotti = null;  //se null, esce warning!? condivisione referenze (come un puntatore) o copia??
		for(int i=0; i < id.length; i++){
			prodotti[i] = new O_Prodotto(nome[i],eccipiente[i],produttore[i],formato[i],quantita[i]);
			System.out.println("OK1");
			magazzinoCentrale.magazzino.put(id[i],prodotti[i]);
			System.out.println("OK2");
		}
	}
	
}
