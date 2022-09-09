package it.polito.tdp.ufo.model;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.ufo.db.SightingsDAO;

public class Model {
	
	private SightingsDAO dao;
	private Graph<String, DefaultEdge> grafo;
	
	public Model() {
		dao = new SightingsDAO();
	}
	
	public void creaGrafo(int anno) {
		grafo = new SimpleDirectedGraph<>(DefaultEdge.class);

		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, dao.getAllStatiByAnno(anno));
		
		// Aggiunta degli archi
		for (String v1 : this.grafo.vertexSet()) {
			for (String v2 : this.grafo.vertexSet()) {
				if (!v1.equals(v2)) {
					if (dao.verticiDaCollegare(v1, v2, anno)) {
						this.grafo.addEdge(v1, v2);
					}
				}
			}
		}
		
	}
	
	public List<String> getStatiPrecedenti(String stato) {
		return Graphs.predecessorListOf(this.grafo, stato);
	}
	
	public List<String> getStatiSuccessivi(String stato) {
		return Graphs.successorListOf(this.grafo, stato);
	}

	public Set<String> getComponenteConnessa(String stato) {
		ConnectivityInspector<String, DefaultEdge> ci = new ConnectivityInspector<>(this.grafo);
		Set<String> componenteConnessa = ci.connectedSetOf(stato);
		return componenteConnessa;
	}
	
	public Set<String> getAllVertici(){
		return this.grafo.vertexSet();
	}
	
	public List<AvvistamentiPerAnno> getAllAvvistamentiPerAnno() {
		return dao.getAllAvvistamentiPerAnno();
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
}
