package it.polito.tdp.ufo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import it.polito.tdp.ufo.model.AvvistamentiPerAnno;
import it.polito.tdp.ufo.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class UfoController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxAnno"
    private ComboBox<AvvistamentiPerAnno> boxAnno; // Value injected by FXMLLoader

    @FXML // fx:id="boxStato"
    private ComboBox<String> boxStato; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void handleAnalizza(ActionEvent event) {
    	String stato = boxStato.getValue();
    	if (stato == null) {
    		txtResult.setText("Per favore selezionare uno stato dalla tendina!");
    		return;
    	}
    	
    	List<String> precedenti = this.model.getStatiPrecedenti(stato);
    	List<String> successivi = this.model.getStatiSuccessivi(stato);
    	Set<String> componenteConnessa = this.model.getComponenteConnessa(stato);
    	txtResult.appendText("STATI PRECEDENTI: \n");
    	txtResult.appendText(precedenti + "\n");
    	
    	txtResult.appendText("STATI SUCCESSIVI: \n");
    	txtResult.appendText(successivi + "\n");
    	
    	txtResult.appendText("Gli altri '" + componenteConnessa.size() + "' stati raggiungibili sono : \n");
    	txtResult.appendText(componenteConnessa + "\n");
    }

    @FXML
    void handleAvvistamenti(ActionEvent event) {
    	boxStato.getItems().clear();
    	
    	AvvistamentiPerAnno anno = boxAnno.getValue();
    	if (anno == null) {
    		txtResult.setText("Per favore selezionare un anno dalla tendina!");
    		return;
    	}
  	
    	
    	Integer annoSelezionato = anno.getAnno();
    	this.model.creaGrafo(annoSelezionato);
    	
    	txtResult.setText("Grafo creato!\n");
    	txtResult.appendText("# Vertici " + this.model.getNumVertici() + "\n");
    	txtResult.appendText("# Archi " + this.model.getNumArchi() + "\n");
    	
    	// Dopo aver creato il grafo, possiamo popolare la tendina degli stati
    	boxStato.getItems().addAll(this.model.getAllVertici());
    	
    }

    @FXML
    void handleSequenza(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Ufo.fxml'.";
        assert boxStato != null : "fx:id=\"boxStato\" was not injected: check your FXML file 'Ufo.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Ufo.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	boxAnno.getItems().addAll(this.model.getAllAvvistamentiPerAnno());
    }
}
