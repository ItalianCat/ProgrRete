/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

/**
 * Questa classe ha il solo scopo di facilitare il test dell'applicazione attraverso il 
 * pre-caricamento nel magazzino centrale di dieci tipologie di prodotti con relativa quantita'.
 * E' il client di tipo Amministratore che dispone di un metodo per caricare nel magazzino 
 * centrale i dati di esempio.
 */
public class Esempio{
	
	public O_Magazzino magazzinoCentrale = null;
	
	/**
	 * Questo costruttore crea un magazzino di esempio utile a scopo di test.
	 */
	public Esempio(){
		magazzinoCentrale = new O_Magazzino();
		String[] id = {"ASP1","ASP2","DRO1","GAV1","HYA1","PLA1","TAC1","TAC2","VAL1","VOL1"};
		String[] nome = {"Aspirine","Aspirine","DropStar","Gaviscon","Hyalistil","Plasil","Tachipirina","Tachipirina","Valium","Voltaren"};
		String[] eccipiente = {"citrato","sodio","sodio","eritrosina","disodio","amido","magnesio","acesulfame","etanolo","gliceridi"};
		String[] produttore = {"BAYER","BAYER","BRACCO","RECKITT","SIFI","LEPETIT","ACRAF","ACRAF","ROCHE","NOVARTIS"};
		String[] formato = {"compresse","bustine","bustine","sciroppo","bustine","sciroppo","sciroppo","supposte","sciroppo","crema"};
		Integer[] quantita = {20,5,7,40,30,43,12,1,14,3};
		O_Prodotto[] prodotti = new O_Prodotto[id.length];
		for(int i=0; i < id.length; i++){
			prodotti[i] = new O_Prodotto(nome[i],eccipiente[i],produttore[i],formato[i],quantita[i]);
			magazzinoCentrale.magazzino.put(id[i],prodotti[i]);
		}
	}
	
}
