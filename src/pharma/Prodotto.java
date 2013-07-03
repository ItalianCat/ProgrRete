package pharma;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Prodotto implements Serializable{
	  public final String nome;
	  public final String eccipiente;
	  public final String produttore;
	  public final String formato;
	  public Integer quantita;

	  public Prodotto(String nome, String eccipiente, String produttore, String formato, Integer quantita) {
		  this.nome = nome;
		  this.eccipiente = eccipiente;
		  this.produttore = produttore;
		  this.formato = formato;
		  this.quantita = quantita;
	  }
	  
	  public Prodotto(Prodotto prodotto, Integer quantita){  //creo un lotto dalla quantita' in stock
		  this.nome = prodotto.nome;
		  this.eccipiente = prodotto.eccipiente;
		  this.produttore = prodotto.produttore;
		  this.formato = prodotto.formato;
		  this.quantita = quantita;
	  }
	  
	  public String toStringProdotto(){
		  String risultato = nome + "\t\t" + eccipiente + "\t\t" + produttore + "\t\t" + formato + "\t\t" + quantita;
		  return risultato;
	  }
}
