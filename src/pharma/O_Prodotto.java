/**
* @author Giuliana Mazzi
* @version 1.0 del 18 luglio 2013
*/
package pharma;

import java.io.Serializable;

/**
 * Questa classe rappresenta un prodotto presente in un magazzino. Contiene tutti i dati 
 * rilevanti relativi ad un prodotto quali: nome, eccipiente principale, produttore, formato 
 * (compresse, sciroppo, supposte, bustine, crema) e quantita'.
 */
@SuppressWarnings("serial")
public class O_Prodotto implements Serializable{
	public final String nome;
	public final String eccipiente;
	public final String produttore;
	public final String formato;
	public Integer quantita;

	/**
	 * Questo costruttore crea un prodotto prendendo i dati da riga di comando dall'amministratore.
	 */
	public O_Prodotto(String nome, String eccipiente, String produttore, String formato, Integer quantita) {
		this.nome = nome;
		this.eccipiente = eccipiente;
		this.produttore = produttore;
		this.formato = formato;
		this.quantita = quantita;
	}
	  
	/**
	 * Questo costruttore consente all'amministratore di rifornire il magazzino con una 
	 * quantita' aggiuntiva di un prodotto gia' codificato. Richiede solo di introdurre 
	 * il codice del prodotto e la quantita' da aggiungere. L'uso di questo costruttore 
	 * evita all'amministratore il bisogno di inserire nuovamente tutti gli altri campi della
	 * codifica del prodotto.
	 */
	public O_Prodotto(O_Prodotto prodotto, Integer quantita){  //creo un lotto dalla quantita' in stock
		this.nome = prodotto.nome;
		this.eccipiente = prodotto.eccipiente;
		this.produttore = prodotto.produttore;
		this.formato = prodotto.formato;
		this.quantita = quantita;
	}	  
	
	/**
	 * Questo metodo consente di visualizzare una riga con tutte le informazioni relative 
	 * ad un prodotto.
	 * @return ritorna una stringa che rappresenta una riga della tabella dei prodotti 
	 * presenti in un magazzino
	 */
	public String toStringProdotto(){
		String risultato = nome + fill(nome.length()) + eccipiente + fill(eccipiente.length()) +
				  produttore + fill(produttore.length()) + formato + fill(formato.length()) + quantita;
		return risultato;
	}
	  
	/**
	  * Questo metodo consente di allineare i campi della tabella contenente i prodotti presenti 
	  * in un magazzino.
	  * @param lung e' la lunghezza della stringa che va inserita in un campo della tabella
	  * @return ritorna una stringa che rappresenta gli spazi vuoti che devono essere interposti 
	  * tra due valori successivi di una riga della tabella
	  */
	private String fill(Integer lung){
		String result = "";
		Integer nVolte = 14 - lung;
		while (nVolte-- > 0)
			result += " ";
		return result;
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof O_Prodotto){
			O_Prodotto otherP = (O_Prodotto)other;
			return otherP.nome.equals(nome) && otherP.eccipiente.equals(eccipiente)
				  && otherP.formato.equals(formato) && otherP.produttore.equals(produttore);
		}
		return false;
	}
		
	@Override
	public int hashCode(){
		return nome.hashCode() + eccipiente.hashCode() + formato.hashCode() + produttore.hashCode();
	}
}
