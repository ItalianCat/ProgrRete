package pharma;

import java.io.Serializable;

@SuppressWarnings("serial")
public class O_Prodotto implements Serializable{
	  public final String nome;
	  public final String eccipiente;
	  public final String produttore;
	  public final String formato;
	  public Integer quantita;

	  public O_Prodotto(String nome, String eccipiente, String produttore, String formato, Integer quantita) {
		  this.nome = nome;
		  this.eccipiente = eccipiente;
		  this.produttore = produttore;
		  this.formato = formato;
		  this.quantita = quantita;
	  }
	  
	  public O_Prodotto(O_Prodotto prodotto, Integer quantita){  //creo un lotto dalla quantita' in stock
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
