package it.polito.tdp.ufo.model;

import java.util.LinkedList;
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
	
	// Variabili per la ricorsione
	private List<String> best;
	
	public Model() {
		dao = new SightingsDAO();
	}
	
	public void creaGrafo(int anno) {
		grafo = new SimpleDirectedGraph<>(DefaultEdge.class);

		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, dao.getAllStatiByAnno(anno));
		
		// Aggiunta degli archi
		// SOLUZIONE POCO OTTIMALE
		for (String v1 : this.grafo.vertexSet()) {
			for (String v2 : this.grafo.vertexSet()) {
				if (!v1.equals(v2)) {
					if (dao.verticiDaCollegare(v1, v2, anno)) {
						this.grafo.addEdge(v1, v2);
					}
				}
			}
		}
		
		/*  SOLUZIONE PIU EFFICIENTE
		 * 
		 *  SELECT s1.state, s2.state
			FROM sighting s1, sighting s2
			WHERE YEAR(s1.datetime) = YEAR(s2.datetime) AND YEAR(s1.datetime) = 2007 AND s1.country = 'us'
			AND s2.country = 'us' AND s2.datetime > s1.datetime AND s1.state <> s2.state
			GROUP BY s1.state, s2.state
		 */
		
	}
	
	public List<String> calcolaCammino(String partenza){
		best = new LinkedList<>();
		
		List<String> parziale = new LinkedList<>();
		parziale.add(partenza);
		
		cerca(parziale);
		
		return best;
	}
	
	private void cerca(List<String> parziale) {
		String ultimo = parziale.get(parziale.size()-1);
		
		if (parziale.size() > best.size()) {
			best = new LinkedList<>(parziale);
		}
		
		// cerchiamo di aggiungere un nuovo vertice
		for (String s : Graphs.successorListOf(this.grafo, ultimo)) {
			if (!parziale.contains(s)) {
				parziale.add(s);
				cerca(parziale);
				parziale.remove(parziale.size()-1); // parziale.remove(s); 
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
