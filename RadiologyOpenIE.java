import java.io.FileReader; 
import java.util.Iterator; 
// import java.util.Map; 
import java.util.Set; 
  import java.io.PrintWriter; 

// import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.Collection;
import java.util.Properties;

import java.util.List;
import java.util.ArrayList;



public class RadiologyOpenIE {
  public static void main(String[] args) throws Exception
   {

     Properties props = new Properties();
    props.setProperty("annotators", "tokenize,pos,lemma,depparse,natlog,ner,openie");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // parsing file "JSONExample.json" 
        Object obj = new JSONParser().parse(new FileReader("reports_fixed.json")); 
          
        // typecasting obj to JSONObject 
        JSONObject jo = (JSONObject) obj;

        // System.out.println(jo.keySet());

        Set<?> s =  jo.keySet();

    Iterator<?> i = s.iterator();
    int counter = 0;
    do{
        String k = i.next().toString();
        // System.out.println(k);
        // System.out.println(jo.get(k).get("OpenIE"));
        JSONObject report = (JSONObject) jo.get(k);

        String findings = (String) report.get("findings");
        String impression = (String) report.get("impression");

        String reportText = findings + ". " + impression;

      List<String> list=new ArrayList<String>();


// Annotate an example document.
    // Annotation doc = new Annotation("Obama was born in Hawaii. He is our president.");
    Annotation doc = new Annotation(reportText);
    pipeline.annotate(doc);

    // Loop over sentences in the document
    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
      // Get the OpenIE triples for the sentence
      Collection<RelationTriple> triples =
	          sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
      

      // Print the triples
      for (RelationTriple triple : triples) {
        // System.out.println(triple.confidence + "\t" +
        //     triple.subjectLemmaGloss() + "\t" +
        //     triple.relationLemmaGloss() + "\t" +
        //     triple.objectLemmaGloss());
         list.add(triple.subjectLemmaGloss());  
         list.add(triple.objectLemmaGloss());  

      }
    }

    list = removeDuplicates(list);

    
    String result = String.join(";", list);



        report.put("OpenIE", result );
        jo.put(k, report);
        // System.out.println(report.get("OpenIE"));
        // jo.put(k, );

        System.out.print("" + counter + "\r");

        counter++;
        if(counter > 5000) break;
    }while(i.hasNext());

    PrintWriter pw = new PrintWriter("reports_fixed.json"); 
        pw.write(jo.toJSONString()); 
          
        pw.flush(); 
        pw.close(); 

        // for (String key: jo.keySet().toArray()){
        //   Object report = jo.get(key);
        //   System.out.println(report);
        // }

  }

  // Function to remove duplicates from an ArrayList 
    public static <T> ArrayList<String> removeDuplicates(List<String> list) 
    { 
  
        // Create a new ArrayList 
        ArrayList<String> newList = new ArrayList<String>(); 
  
        // Traverse through the first list 
        for (String element : list) { 
  
            // If this element is not present in newList 
            // then add it 
            if (!newList.contains(element)) { 
  
                newList.add(element); 
            } 
        } 
  
        // return the new list 
        return newList; 
    }
}