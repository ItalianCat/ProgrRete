Per avviare correttamente l'applicazione:

1) Scrivere l'ip dell'host dei server nel file "IPserver.txt" nella cartella javarmi
mministratore

2) Scrivere il percorso assoluto per arrivare alla cartella javarmi nel file Input.java

3) Aggiornare i codebase nei tre file di policy relativi ai client: amm.policy, cliente.policy e farm.policy


***********LATO SERVER***********
4) Mettere il file keystore.jks nella cartella sopra javarmi e truststore.jks in javarmi


5) Eseguire i comandi riportati di seguito (= compilazione di tutti i file in javarmi/pharma,
creazione degli stub con destinazione public_html/common, copia di tutti i file compilati in public_html/common).

rm -r /home/accounts/studenti/id585uee/public_html/common/pharma
cd javarmi/pharma
javac *.java
cd ..
rmic -d ~/public_html/common/ pharma.Server
rmic -d ~/public_html/common/ pharma.ServAutenticazione
rmic -d ~/public_html/common/ pharma.ServProxy
rmic -iiop -d ~/public_html/common/ pharma.ServProxy
rmic -d ~/public_html/common/ pharma.ServBootstrap
rmic -iiop -d ~/public_html/common/ pharma.ServBootstrap
cp /home/accounts/studenti/id585uee/javarmi/pharma/*.class /home/accounts/studenti/id585uee/public_html/common/pharma/


6) In un primo terminale avviare il server http:

cd public_html
python -m SimpleHTTPServer


7) In un secondo terminale avviare il demone di attivazione:

rmid -log /home/accounts/studenti/id585uee/javarmi/pharma/log-rmid/ 
	-J-Djava.rmi.server.codebase=http://157.27.241.177:8000/common/ 
	-J-Djava.security.policy=/home/accounts/studenti/id585uee/javarmi/pharma/rmid.policy


8) In un terzo terminale avviare i registri e il setup:

cd javarmi
unset CLASSPATH
rmiregistry 1099 &
tnameserv -ORBInitialPort 5555 &
java -classpath :/home/accounts/studenti/id585uee/public_html/common/ 
	-Djava.rmi.server.codebase=http://157.27.241.177:8000/common/ 
	-Djava.security.policy=/home/accounts/studenti/id585uee/javarmi/pharma/setup.policy pharma.Setup



***********LATO CLIENT***********

9) Mettere in javarmi il file IPserver.txt e in javarmi/pharma i seguenti file:
MainClientIIOP.class
MainClientJRMP.class
ServBootstrap_I.class
amm.policy
cliente.policy
farm.policy

10) Da javarmi avviare il client coi comandi seguenti in base al tipo:
Per amministratore:
java -Djava.security.policy=pharma/amm.policy pharma.MainClientIIOP
Per farmacia:
java -Djava.security.policy=pharma/farm.policy pharma.MainClientJRMP
Per cliente finale
java -Djava.security.policy=pharma/cliente.policy pharma.MainClientJRMP


Giuliana Mazzi
17 luglio 2013