/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2012.
 *
 */
package fr.inra.mig.cdxws.db

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import org.squeryl._
import org.squeryl.dsl._
import org.jgrapht._
import org.jgrapht.graph._
import org.jgrapht.alg._

import org.squeryl.PrimitiveTypeMode._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON._

import CadixeDB._
import AnnotationSetType._

object Utils {

  @Deprecated
  def init() = {
    import net.liftweb.json._
    import Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)
    Session.currentSession.setLogger(println(_))
    create
    CadixeDB.addUser("aae_root","##?##",true)
    val foo = User("foo","foo",false)
    users.insert(foo)
    val bar = User("bar","bar",false)
    users.insert(bar)
    val cadixe = User("cadixe","cadixe",true)
    users.insert(cadixe)
    val biotopes = Campaign("Biotopes", """{
    "taxon": {
      "kind": 0,
      "type": "taxon",
      "color": "#7FFF00",
      "propDef":{
        "taxid": {
          "key": "taxid",
          "mandatory" : true,
          "minVal" : 1,
          "maxVal" : 1,
          "valType" : null
        }
      },
      "txtBindingDef": {
        "minFrag" : 1,
        "maxFrag" : 1,
        "boundRef" : null,
        "crossingAllowed" : true
      }
    },
    "protein": {
      "kind": 0,
      "type": "protein",
      "color": "#FF69B4",
      "propDef":{
      },
      "txtBindingDef": {
        "minFrag" : 1,
        "maxFrag" : 1,
        "boundRef" : null,
        "crossingAllowed" : false
      }
    },
    "gene": {
      "kind": 0,
      "type": "gene",
      "color": "#87CEFA",
      "propDef":{
      },
      "txtBindingDef": {
        "minFrag" : 1,
        "maxFrag" : 1,
        "boundRef" : null,
        "crossingAllowed" : true
      }
    },
    "taxon_coreference": {
      "kind": 1,
      "type": "taxon_coreference",
      "color": "green",
      "propDef":{
        "taxid": {
          "key": "taxid",
          "mandatory" : true,
          "minVal" : 1,
          "maxVal" : 1,
          "valType" : null
        }
      },
      "groupDef": {
          "minComp" : 1,
          "maxComp" : 9999999,
          "compType" : ["taxon"],
          "homogeneous" : true
      }
    },
    "gene_protein": {
      "kind": 1,
      "type": "gene_protein",
      "color": "orange",
      "propDef":{
      },
      "groupDef": {
          "minComp" : 1,
          "maxComp" : 9999999,
          "compType" : ["gene", "protein"],
          "homogeneous" : true
      }
    },
    "lives_in": {
      "kind": 2,
      "type": "lives_in",
      "color": "#DC143C",
      "propDef": {
      },
      "relationDef": {
          "bacteria": ["taxon", "taxon_coreference"],
          "host": ["taxon", "taxon_coreference"]
      }
    },
    "expressed_in": {
      "kind": 2,
      "type": "expressed_in",
      "color": "cyan",
      "propDef": {
      },
      "relationDef": {
          "product": ["protein", "gene"],
          "species": ["taxon"]
      }
    },
    "ternary": {
      "kind": 2,
      "type": "ternary",
      "color": "#B8860B",
      "propDef": {
        "some_property": {
          "key": "some_property",
          "mandatory" : false,
          "minVal" : 1,
          "maxVal" : 1,
          "valType" : {  "closedDomain" : true, "domain" : ["true", "false"], "defaultVal" : "true"}
        }
      },
      "relationDef": {
          "member1": ["taxon"],
          "member2": ["taxon"],
          "member3": ["taxon"]
      }
    }
      }
""")
    campaigns.insert(biotopes)
    campaign_annotators.insert(CampaignAnnotator(biotopes.id,foo.id))
    campaign_annotators.insert(CampaignAnnotator(biotopes.id,bar.id))

    val doc = Document(foo.id,
                       write(Map()),
                       "Abstractn-Dodecane and fatty acids were good inducers of cytochrome P450 (CYP) and the ω-hydroxylase of lauric acid, which is a marker for ω-hydroxylation of n-alkanes, in Trichoderma harzianum. A cDNA, containing an ORF of 1520 bp, encoding a CYP52 of 520 amino acids, was isolated by RACE. Another n-alkane-inducible CYP was identified by LLC-MS/MS analysis of a microsomal protein band induced by n-dodecane in a library of T. harzianum. This suggests that T. harzianum has a CYP-dependent conversion of alkanes to fatty acids allowing their incorporation into lipids.",
                       "Test document",
                       "A random PubMed document")
    documents.insert(doc)
    document_assignment.insert(DocumentAssignment(biotopes.id,foo.id,doc.id,Some(now()),None))
    document_assignment.insert(DocumentAssignment(biotopes.id,bar.id,doc.id,None,None))
    val as_doc = AnnotationSet(doc.id,foo.id,biotopes.id,
                               write(List(TextAnnotation("5d17041f",Map(),List(List(87,100)),"protein"))),
                               write(Nil),
                               write(Nil),
                               true, 0,
                               UserAnnotation,
                               "Foo's Annotation", now())
    annotation_sets.insert(as_doc)
    val as_html_doc = AnnotationSet(doc.id,cadixe.id,biotopes.id,
                                    write(List(TextAnnotation("fmt-01",Map(),List(List(0,8)),"h3"))),
                                    write(Nil),
                                    write(Nil),
                                    true, 0,
                                    HtmlAnnotation, "HTML formatting", now())
    annotation_sets.insert(as_html_doc)
  }


  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  val inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
  case class Properties(var props : Map[String,List[String]])
  case class DocumentExt(id : Long, owner : Long, contents : String, props : Map[String,List[String]], description : String)
  case class AnnotationSetExt(id : Long, owner : Long,
                              text_annotations : List[TextAnnotation], groups : List[Group], relations : List[Relation],
                              head : Boolean, revision : Int, `type` : String,
                              description : String, timestamp : String)


  //Annotation schema related data structures
  case class PropertyValType(valTypeName : String, TyDIRefBaseURL : Option[String], domain : Option[List[String]], defaultVal : Option[String])
  case class PropertyDefinition(key : String, mandatory : Boolean, minVal : Int, maxVal : Int, valType : PropertyValType)
  type PropertiesDefinition = Map[String,PropertyDefinition]
  case class TextBinding (minFrag: Int, maxFrag: Int, boundRef: Option[List[Any]], crossingAllowed : Boolean)
  case class GroupDefinition (minComp: Int, maxComp: Int, compType: List[String], homogeneous : Boolean)
  type RelationDefinition = List[Map[String, List[String]]]
  case class AnnotationType(kind : Int, `type`: String, color : String,
                            propDef : Option[PropertiesDefinition],
                            txtBindingDef : Option[TextBinding],
                            groupDef : Option[GroupDefinition],
                            relationDef : Option[RelationDefinition])
  type SchemaDefinition = Map[String, AnnotationType]

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  val FIELD_SEPARATOR = ", ";
  val FIELD_DELIMITER = "\"";
  val LINE_TERMINATOR = "\n";
  val FRAGMENT_SEPARATOR = "";

  val DOCPROP_FILENAME = "filename";
  val DOCPROP_ALVISNLPID = "AlvisNLPID";


  /**
   * Retrieve the text of the specified TextAnnotation, and a string representation of its Fragment(s) coordinates
   */
  def getTextAnnotationTextNCoord(netText : String, annotation : TextAnnotation) : (String, String) = {
    var once = false
    val coord = new StringBuilder()
    val texte = new StringBuilder()
    annotation.text.foreach( frag => {
        if (once) {
          coord.append(", ");
          texte.append(FRAGMENT_SEPARATOR);
        } else {
          once = true;
        }
        val start = frag(0)
        val end = frag(1)
        coord.append("[").append(start).append(", ").append(end).append("]")
        texte.append(netText.substring(start, end));
      }
    )
    return (texte.toString, coord.toString)
  }


  /**
   * Export all annotation contained within a Campaign as CSV text files
   * @param outputDir absolute path of the directory where the files will be created.
   * @param campaignId identifier of the Campaign to be exported.
   */
  def exportCampaignAnnotationAsCSV(outputDir : String, campaignId : Long) {
    Console.println("")

    //parameter checking
    val outDir = (
      Option(outputDir) match {
        case None =>
          Console.err.println("Missing parameter output directory! Export aborted.")
          return Unit
        case Some(dir) =>
          val outDir = new File(dir)
          if (! outDir.exists || !outDir.isDirectory) {
            Console.err.println("Invalid parameter output directory " + dir + " ! Export aborted.")
            return Unit
          } else if (! outDir.canWrite) {
            Console.err.println("Unable to write in output directory " + dir + " ! Export aborted.")
            return Unit
          }
          outDir
      }
    )
    val campaign = (
      Option(campaignId) match {
        case None =>
          Console.err.println("Missing parameter Campaign Id! Export aborted.")
          return Unit
        case Some(cId) =>
          try {
            campaigns.lookup(cId).get
          } catch {
            case ex:NoSuchElementException =>
              Console.err.println("Unknown campaignId (" +  campaignId + ")! Export aborted")
              return Unit
          }
      }
    )

    //retrieve all head Annotation Sets (except Formatting ones) associated to documents within the Campaign
    val annotationSetToExport = from(documents,document_assignment,annotation_sets)((d,da,as) =>
      where(    da.campaign_id === campaign.id
            and da.doc_id === d.id
            and as.doc_id === da.doc_id
            and as.campaign_id === da.campaign_id
            and as.user_id === da.user_id
            and as.head === true
            and as.`type` <> HtmlAnnotation).select(d, da, as).orderBy(da.doc_id, da.user_id, as.`type`)).distinct.toList

    import net.liftweb.json._
    import net.liftweb.json.JsonDSL._
    import net.liftweb.json.Serialization.write

    var previousDoc : Document = null;
    val line = new StringBuilder();

    try {
      var out : OutputStreamWriter = null
      var nbAS = 0
      var nbText = 0
      var nbGrp = 0
      var nbRel = 0
      var outputfilename : String = null

      var nbFile = 0
      //loop over Annotation Sets
      for(d_as <- annotationSetToExport) {
        val d = d_as._1
        val da = d_as._2
        val as = d_as._3

        if (out==null || previousDoc.id!=d.id) {

          //display some detail about the last generated file
          if (out!=null) {
            out.close()
            Console.print("\t" + nbText + " TextAnnotation(s)\t"+ nbGrp + " Group(s)\t"+ nbRel + " Relation(s)\twithin "+ nbAS + " AnnotationSet(s)\n")
          }
          nbText = 0
          nbGrp = 0
          nbRel = 0
          nbAS = 0

          //An output file contains all Annotation Sets related to one single document
          outputfilename = "ExportAlvisAE_c" + da.campaign_id + "_d" + da.doc_id + ".cvs"
          Console.print("New File: " + outputfilename)

          val fos = new FileOutputStream(outDir.getAbsolutePath() + File.separatorChar + outputfilename)
          out = new OutputStreamWriter(fos, "utf-8")
          out.append("Document=" + d.id + " - \"" + d.description + "\"").append(LINE_TERMINATOR).append(LINE_TERMINATOR).append(LINE_TERMINATOR)
          nbFile += 1
        }

        previousDoc = d
        nbAS += 1

        out.append("AnnotationSetId=" + as.id + ", type=\"" + as.`type`.toString + "\"" + ", userId=" + da.user_id).append(LINE_TERMINATOR).append(LINE_TERMINATOR)

        val txt_ann = parse(as.text_annotations).extract[List[TextAnnotation]]
        out.append("ID, KIND, TYPE, TEXT, FRAGMENTS").append(LINE_TERMINATOR)
        txt_ann.foreach(annotation =>
          {
            line.clear()
            line.append(annotation.id).append(FIELD_SEPARATOR)
            line.append(AnnotationKind.TextAnnotation.toString).append(FIELD_SEPARATOR)
            line.append(FIELD_DELIMITER).append(annotation.`type`).append(FIELD_DELIMITER).append(FIELD_SEPARATOR)
            val (texte, coord) = getTextAnnotationTextNCoord(previousDoc.contents, annotation)
            line.append(FIELD_DELIMITER).append(texte).append(FIELD_DELIMITER).append(FIELD_SEPARATOR)
            line.append(FIELD_DELIMITER).append(coord).append(FIELD_DELIMITER).append(FIELD_SEPARATOR)
            line.append(LINE_TERMINATOR)
            out.append(line.toString)
            nbText += 1
          }
        )
        out.append(LINE_TERMINATOR)

        val grp_ann = parse(as.groups).extract[List[Group]]
        out.append("ID, KIND, TYPE, GROUP_COMPONENTS").append(LINE_TERMINATOR)
        grp_ann.foreach(annotation =>
          {
            line.clear()
            line.append(annotation.id).append(FIELD_SEPARATOR)
            line.append(AnnotationKind.GroupAnnotation.toString).append(FIELD_SEPARATOR)
            line.append(FIELD_DELIMITER).append(annotation.`type`).append(FIELD_DELIMITER).append(FIELD_SEPARATOR)

            annotation.group.foreach( annRef =>
              line.append(annRef.ann_id).append("\t")
            )
            line.append(LINE_TERMINATOR)
            out.append(line.toString)
            nbGrp += 1
          }
        )
        out.append(LINE_TERMINATOR)

        val rel_ann = parse(as.relations).extract[List[Relation]]
        out.append("ID, KIND, TYPE, RELATION_ARGUMENTS").append(LINE_TERMINATOR)
        rel_ann.foreach(annotation =>
          {
            line.clear()
            line.append(annotation.id).append(FIELD_SEPARATOR)
            line.append(AnnotationKind.RelationAnnotation.toString).append(FIELD_SEPARATOR)
            line.append(FIELD_DELIMITER).append(annotation.`type`).append(FIELD_DELIMITER).append(FIELD_SEPARATOR)

            annotation.relation.map( { case(role, annRef) =>
                  line.append(role).append(": ")
                  line.append(annRef.ann_id).append("\t")
              }
            )
            line.append(LINE_TERMINATOR)
            out.append(line.toString)
            nbRel += 1
          }
        )
        out.append(LINE_TERMINATOR)

      }

      out.close()
      //display some details about the last generated file
      Console.print("\t" + nbText + " TextAnnotation(s)\t"+ nbGrp + " Group(s)\t"+ nbRel + " Relation(s)\twithin "+ nbAS + " AnnotationSet(s)\n")

      //display some detail about the overall exportation process
      Console.print("\n " + nbFile + " file(s) exported.")

    } catch  {
      case ex:UnsupportedEncodingException =>
        ex.printStackTrace(Console.err)
      case ex:FileNotFoundException =>
        ex.printStackTrace(Console.err)
      case ex:IOException =>
        ex.printStackTrace(Console.err)
    }

  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  /**
   * Create a new Campaign defined by the specified parameter file
   * @param parameterFile absolute path to the file containing Campaign parameters.
   * First line of the file will be used for the description of the Campaign ("name" field);
   * Second line contains the path to a json file containing the Annotation Schema for the campaign,
   * Each one of the following lines contain the path to a JSON document to import in the campaign
   *
   */
  //def createNewCampaignFromTextDocs(parameterFile : String, owner : User, user : User) : Unit = {
  //}


  /**
   * Create a new Campaign defined by the specified parameter file
   * @param parameterFile absolute path to the file containing Campaign parameters.
   * First line of the file will be used for the description of the Campaign ("name" field);
   * Each one of the following lines contain the path to a JSON document to import in the campaign
   *
   */
  def createNewCampaignFromJsonDocs(parameterFile : String, user : User) : Campaign = {
    try {
      val owner = Utils.getUserByLogin("aae_root");
      if (owner==null) {
        Console.err.println("SEVERE ERROR - Missing values for Owner! Import aborted.")
        return null
      }
      if (user==null) {
        Console.err.println("Missing values for User! Import aborted.")
        return null
      }

      val source = io.Source.fromFile(parameterFile)
      val params = source.getLines().toList
      source.close()

      if (params.length<2) {
        Console.err.println("Missing values in parameter file! Import aborted.")
        return null
      }
      val description = params(0)
      val docFiles = params.drop(1)


      //retrieve Annotation schema from first document
      val schema = retrieveSchema(docFiles(0));
      if (schema.isEmpty) {
        Console.err.println("Missing annotation schema! Import aborted.")
        return null
      }
      Console.println("Annotation Schema retrieved!")

      //ensure unicity of Campaign description
      if (getCampaignByName(description)!=null) {
        Console.err.println("This description is already used for an existing campaign! Import aborted.")
        return null
      }

      //import is performed within one single DB transaction
      ////FIXME transaction not supported ????
      //transaction {
      val campaign = Campaign(description, schema)
      campaigns.insert(campaign)
      Console.println("New Campaign created ("+ campaign.id)

      import scala.collection.mutable.Set
      docFiles.foreach(f=> {
          Console.println("\timporting "+ f + " ...")
          processJSONFile(f,
                          campaign,
                          owner.id,
                          user.id,
                          Set(user.id) )
          Console.println(" done")
        })

      return campaign
      //}

    } catch {
      case ex =>
        Console.err.println("Import aborted :")
        ex.printStackTrace(Console.err)
        return null
    }
  }

  def retrieveSchema(filepath : String) : String = {
    import net.liftweb.json.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val source = io.Source.fromFile(filepath);
    val jsonText = source.mkString
    source.close()

    val parsed = net.liftweb.json.JsonParser.parse(jsonText)
    val jsSchema = parsed \ "schema"

    jsSchema match {
      case JNothing => "";
      case _ => 
        checkSchemaConformance(jsSchema)
        compact(render(jsSchema))
    }
  }


  case class InvalidSchemaException(message : String) extends IllegalArgumentException(message : String)

  /**
   * Perform syntax and conformance checking of the specified JSON annotation schema.
   *
   * @throw InvalidSchemaException 
   */
  def checkSchemaConformance(schema : JValue) : Unit = {

    val schemaDefinition = try {
      schema.extract[SchemaDefinition]
    } catch {
      case ex:MappingException => throw new InvalidSchemaException("Something other than a list of types was found:\n\n" + ex.getMessage())
    }

    val referentialGraph : DefaultDirectedGraph[String,DefaultEdge] = new DefaultDirectedGraph(classOf[DefaultEdge])

    //1rst pass just retrieve the AnnotationType names
    schemaDefinition.map(annTypeEntry => {
        val annTypeName = annTypeEntry._1

        if (!referentialGraph.addVertex(annTypeName)) {
          throw new InvalidSchemaException("Duplicate AnnotationType name:\t" + annTypeName)
        }
      })

    //2nd pass perform some referential checking
    schemaDefinition.map(annTypeEntry => {
        val annTypeName = annTypeEntry._1
        val annotationType = annTypeEntry._2


        if (!annotationType.`type`.equals(annTypeName)) {
          throw new InvalidSchemaException("Annotation Type mismatch:\t'" + annTypeName + "' <> '" + annotationType.`type` +"'")
        }

        //Console.out.print("Name:\t" + annTypeName + "\t")
        
        val colour = annotationType.color
        if (!colour.matches("#[0-9A-Fa-f]{6}")) {
          throw new InvalidSchemaException("Invalid color declaration '" + colour + "' for AnnotationType '" + annTypeName  + "'")
        }
        
        annotationType.propDef match {
          case Some(propertiesDef) =>
              
            propertiesDef.map(entry => {
                val propName = entry._1
                val propDef = entry._2

                if (!propDef.key.equals(propName)) {
                  throw new InvalidSchemaException("Property name mismatch:\t'" + propName + "' <> '" + propDef.key + "' for AnnotationType '" + annTypeName  + "'")
                }

                if (propDef.minVal<=0) {
                  throw new InvalidSchemaException("Invalid minimum number of property values in for AnnotationType '" + annTypeName  + "' [" + propName + "]")
                } else if (propDef.minVal >propDef.maxVal) {
                  throw new InvalidSchemaException("Invalid maximum number of property values in for AnnotationType '" + annTypeName  + "' [" + propName + "]")
                }
                
                //properties value type definition is optional
                val valTypeDef = propDef.valType
                if (valTypeDef!=null) {
                  valTypeDef.valTypeName match {
                    case "ClosedDomain" =>
                      val domainValues = valTypeDef.domain match {
                        case Some(domainValues) => domainValues
                        case _ =>
                          throw new InvalidSchemaException("Missing domain values in property '" + propName + "' definition for Text AnnotationType '" + annTypeName  + "'")
                      }
                      
                      if (domainValues.isEmpty) {
                          throw new InvalidSchemaException("Empty domain values in property '" + propName + "' definition for Text AnnotationType '" + annTypeName  + "'")
                      }

                      valTypeDef.defaultVal match {
                        case Some(value) => if (!domainValues.contains(value)) {
                            throw new InvalidSchemaException("Unknown default value ("+value +") in property '" + propName + "' definition for Text AnnotationType '" + annTypeName  + "'")
                          }
                        case _ =>
                          throw new InvalidSchemaException("Missing default values in property '" + propName + "' definition for Text AnnotationType '" + annTypeName  + "'")
                        
                      }
                      
                    case "TyDI_termRef" | "TyDI_semClassRef" =>
                      valTypeDef.TyDIRefBaseURL match {
                        case Some(tydiURL) => try {
                            val url = new URL(tydiURL)
                          } catch {
                            case ex:MalformedURLException =>
                              throw new InvalidSchemaException("Malformed TyDIRefBaseURL in property '" + propName + "' definition for Text AnnotationType '" + annTypeName  + "'")
                          }
                        case _ => 
                          throw new InvalidSchemaException("Missing TyDIRefBaseURL in property '" + propName + "' definition for Text AnnotationType '" + annTypeName  + "'")
                      }
                      
                    case other => 
                      throw new InvalidSchemaException("Unrecognized property value type '"+other + "' declared in AnnotationType '" + annTypeName  + "'")
                  }
                    
                }
                
              })
            
          case _ =>
            //properties definition is optional
        }

        if (AnnotationKind.TextAnnotation.id.equals(annotationType.kind)) {

          annotationType.txtBindingDef match {
            case Some(textBinding : TextBinding) =>

              if (textBinding.minFrag<=0) {
                throw new InvalidSchemaException("Invalid minimum number of fragment in TextBinding definition for AnnotationType '" + annTypeName  + "'")
              } else if (textBinding.minFrag>textBinding.maxFrag) {
                throw new InvalidSchemaException("Invalid maximum number of fragment in TextBinding definition for AnnotationType '" + annTypeName  + "'")
              }

            case None =>
              throw new InvalidSchemaException("Missing TextBinding definition for Text AnnotationType '" + annTypeName  + "'")
            case _ =>
              throw new InvalidSchemaException("Something other than a valid TextBinding definition for AnnotationType '" + annTypeName  + "'")

          }

        } else if (AnnotationKind.GroupAnnotation.id.equals(annotationType.kind)) {

          annotationType.groupDef match {
            case Some(groupDef : GroupDefinition) =>
              if (groupDef.minComp<=0) {
                throw new InvalidSchemaException("Invalid minimum number of component in Group definition for AnnotationType '" + annTypeName  + "'")
              } else if (groupDef.minComp>groupDef.maxComp) {
                throw new InvalidSchemaException("Invalid maximum number of component in Group definition for AnnotationType '" + annTypeName  + "'")
              } else if (groupDef.compType.isEmpty) {
                throw new InvalidSchemaException("Missing allowed component type(s) in Group definition for AnnotationType '" + annTypeName  + "'")
              }

              groupDef.compType.foreach(referencedType =>
                try {
                  if (referentialGraph.addEdge(annTypeName, referencedType)==false) {
                    throw new InvalidSchemaException("AnnotationType '" + referencedType  + "' is already referenced from Group definition of AnnotationType '" + annTypeName  + "'")
                  }
                } catch {
                  case ex:IllegalArgumentException => throw new InvalidSchemaException("Missing referenced AnnotationType '" + referencedType  + "' declared in Group definition of AnnotationType '" + annTypeName  + "'")
                })

            case None =>
              throw new InvalidSchemaException("Missing Group definition for Group AnnotationType '" + annTypeName  + "'")
            case _ =>
              throw new InvalidSchemaException("Something other than a valid Group definition for AnnotationType '" + annTypeName  + "'")
          }

        } else if (AnnotationKind.RelationAnnotation.id.equals(annotationType.kind)) {

          annotationType.relationDef match {
            case Some(relationDef : RelationDefinition) =>

              if (relationDef.isEmpty) {
                throw new InvalidSchemaException("Invalid number of argument (0) in Relation definition for AnnotationType '" + annTypeName  + "'")
              }
              val argumentames = relationDef.map( arg => {
                  if (arg.keySet.size!=1) {
                    throw new InvalidSchemaException("Invalid number of argument name in Relation definition for AnnotationType '" + annTypeName  + "'\n" + arg.keySet.toString)
                  }

                  //Map with only one entry
                  val argName = arg.keySet.toList(0)
                  arg.get(argName) match {
                    case Some(argTypes) => 
                      if (argTypes.isEmpty) {
                        throw new InvalidSchemaException("Empty referenced types set from Relation definition of AnnotationType '" + annTypeName  + "' (argument= '" + argName+"')\n\n")
                      }
                        
                      argTypes.foreach(referencedType =>
                        try {
                          //Relation AnnotationType can reference the same AnnoationType in distinct arguments
                          referentialGraph.addEdge(annTypeName, referencedType)
                        } catch {
                          case ex:IllegalArgumentException => throw new InvalidSchemaException("Missing referenced AnnotationType '" + referencedType  + "' declared in Relation definition of AnnotationType '" + annTypeName  + "' (argument= '" + argName+"')")
                        })

                    case other =>
                      throw new InvalidSchemaException("Missing referenced type(s) from Relation definition of AnnotationType '" + annTypeName  + "' (argument= '" + argName+"')\n\n" + other)
                  }
                })
            case None =>
              throw new InvalidSchemaException("Missing Relation definition for Relation AnnotationType '" + annTypeName  + "'")
            case _ =>
              throw new InvalidSchemaException("Something other than a valid Relation definition for AnnotationType '" + annTypeName  + "'")
          }

        } else {
          throw new InvalidSchemaException("Something other than a valid annotation kind was found for AnnotationType '" + annTypeName  + "'")
        }
      })
    
    //3rd check that there is no cyclic referencing in AnnotationTypes
    val cd = new CycleDetector(referentialGraph)
    if (cd.detectCycles) {
      val cmsg = cd.findCycles().toArray.foreach(cycle => 
        throw new InvalidSchemaException("Some cyclic referencing has been found in this schema (at least the following type is involved: " + cycle + ")")
      )
    }
    
  }

  def getUserByLogin(userLogin : String ) : User = {
    val user =  from(users)((u) => where(u.login === userLogin) select(u)).headOption match {
      case Some(u) => u
      case None => null
    }
    user
  }

  def getRootUser() : User = {
    Utils.getUserByLogin("aae_root")
  }

  def getCampaignByName(name : String ) : Campaign = {
    val campaign =  from(campaigns)((c) => where(c.name === name) select(c)).headOption match {
      case Some(c) => c
      case None => null
    }
    campaign
  }

  def getCampaignById(campaign_id : Long ) : Campaign = {
    val campaign =  from(campaigns)((c) => where(c.id === campaign_id) select(c)).headOption match {
      case Some(c) => c
      case None => null
    }
    campaign
  }


  def assignUsersToCampaign(campaign_id : Long, assignees : Set[Long] )  {
    val documents = from(campaign_documents)( (cd) =>
      where(cd.campaign_id === campaign_id)
      select(cd))

    assignees.foreach(
      user_id => {
        CadixeDB.campaign_annotators.insert(CampaignAnnotator(campaign_id,user_id))
        documents.foreach(
          cdoc => {
            CadixeDB.document_assignment.insert(DocumentAssignment(campaign_id, user_id, cdoc.doc_id, Some(now()), None))
          })
      }
    )
  }

  /**
   * Import into the database an AnnotatedText in JSON format.
   * The expected document must contain only one Annotation Set whose type is "UserAnnotation"
   *
   * @param filepath absolute path to the file containing the JSON document
   * @param ownerId Id of the user owning the document.
   * Important Notice : it MUST be different from userId and any assignees otherwise unexpected result could happen when retrieving data.
   * @param campaignId the campaign to which the imported document will be associated
   * @param userId the user to which the AnnotationSet of type "UserAnnotation" will be associated
   * @param assignees Set of users which will be assigned to this document (i.e. can annotate it)
   */
  def loadFileInDB(filepath : String,  format : String, ownerId : Long, campaignId : Long, userId : Long, assignees : Set[Long]) = {
    var doc : Document = null;
    var checkedId = ownerId
    try {
      import scala.collection.mutable.Set

      val userIds = Set(ownerId, userId)
      assignees.foreach( aId => userIds += aId )

      userIds.foreach( uId => {
          checkedId = uId
          users.lookup(uId).get
        })

      try {
        val campaign = campaigns.lookup(campaignId).get

        if (ownerId==userId || assignees.contains(ownerId)) {
          Console.err.println("Owner must not be one of the assignee!")
          Console.err.println("Import aborted")

        } else {
          Console.println("Importing... " + filepath)

          //owner MUST NOT be in the assignee
          userIds.remove(ownerId)
          if (format=="JSON") {
            doc = processJSONFile(filepath, campaign, ownerId, userId, userIds)
          } else {
            doc = processTextFile(filepath, campaign, ownerId, userId, userIds)
          }
        }
      } catch {
        case ex:NoSuchElementException =>
          Console.err.println("Unknown campaignId (" +  campaignId + ")")
          Console.err.println("Import aborted")
      }

    } catch {
      case ex:NoSuchElementException =>
        Console.err.println("Unknown userId (" +  checkedId + ")")
        Console.err.println("Import aborted")
    }
    doc
  }
  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  def rewriteAnnotationRef(reference : AnnotationReference, oldAnnSetId :Long, annSetIdsMap : Map[Long, Long]) = {
    reference.set_id match {
      case Some(setId) => {
          //if the specified Set Id corresponds to the current AnnotationSet, then remove the value
          if (setId == oldAnnSetId) {
            reference.set_id = None
          } else {
            //the specified Set Id corresponds to another AnnotationSet, then the id must be replaced by the new internal Set Id
            annSetIdsMap.get(setId) match {
              case Some(newSetId) =>  reference.set_id = Some(newSetId)
              case _ => //happens only if the imported file contains invalid references :
                //FIXME : abort import loudly, and cancel transaction
            }
          }
        }
      case _ =>
    }
    Unit
  }

  trait PropsGenerator {
    def getUpdatedProps(properties : Properties) : (Boolean, Properties)
  }

  class AddHLinkPropsGenerator(prefix :String, suffix : String) extends PropsGenerator {

    def getUpdatedProps(properties : Properties) = {
      var updated = false;
      var result = properties;

      result.props.get("shortName") match {
        case Some(shortname) => {
            if (shortname.length>0) {
              result.props += "hlink" -> List(prefix + shortname(0) + suffix)
              updated = true;
            }
          }
        case None => ;
      }
      (updated, result)
    }
  }

  def updateDocumentProps(campaignId : Long, generator : PropsGenerator) {
    import net.liftweb.json.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val campaign = campaigns.lookup(campaignId).get

    //retrieve documents in the campaign
    val docs = from(documents,campaign_documents)((d,cd) =>
      where(cd.campaign_id===campaign.id and d.id === cd.doc_id)
      select(d)
    )

    //loop over every documents of the campaign
    docs.foreach(d => {
        val parsed = net.liftweb.json.JsonParser.parse("{ \"props\": " + d.props + " }")
        val properties = parsed.extract[Properties]
        //generate new properties
        val (updated, upProps) = generator.getUpdatedProps(properties)
        Console.println(upProps);
        if (updated) {
          //update Document in DB if necessary
          d.props = write(upProps.props)
          documents.update(d)
        }
      })
  }


  /*
   def updateSchema(campaignId : Long, filepath : String) {
   import net.liftweb.json.Serialization.write
   implicit val formats = Serialization.formats(NoTypeHints)

   val campaign = campaigns.lookup(campaignId).get

   val jsonText = io.Source.fromFile(filepath).mkString
   val parsed = net.liftweb.json.JsonParser.parse(jsonText)
   val jsSchema = parsed \ "schema"
   val schema = compact(render(jsSchema))
   campaign.schema = schema
   campaigns.update(campaign)
   }
   */

  def processJSONFile(filepath : String , campaign : Campaign, ownerId : Long, userId : Long, assignees : scala.collection.mutable.Set[Long]) = {
    import net.liftweb.json.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val source = io.Source.fromFile(filepath)
    val jsonText = source.mkString
    source.close()

    val parsed = net.liftweb.json.JsonParser.parse(jsonText)

    //Import Document part
    val jsDoc = parsed \ "document"
    val docExt = jsDoc.extract[DocumentExt]

    //add imported file path to the Document properties
    var properties = docExt.props
    properties.get(DOCPROP_FILENAME) match {
      case Some(path) => {
          //prepend new value of filename to the existing ones
          properties -- List(DOCPROP_FILENAME)
          properties += DOCPROP_FILENAME ->  (filepath :: path)
        }
      case None => {
          //create new entry for filename
          properties += DOCPROP_FILENAME -> List(filepath)
        }
    }

    // extract AlvisNLPID
    val alvisNLPId = properties.get(DOCPROP_ALVISNLPID) match {
      case Some(ids) => {
          if (!ids.isEmpty) {
            Option(ids(0));
          } else {
            None;
          }
        }
      case None => None;
    }

    //create document record
    val doc =  Document(ownerId,
                        write(properties),
                        docExt.contents,
                        "", //comment
                        docExt.description)
    documents.insert(doc)

    //associate document to campaign with optional AlvisNLP_ID
    campaign_documents.insert(CampaignDocument(campaign.id,doc.id, alvisNLPId))

    //asscociate users to document
    assignees.foreach( aId => document_assignment.insert(DocumentAssignment(campaign.id, aId, doc.id, Some(now()), None)) )

    //Import AnnotationSets
    val jsAnnSets = parsed \ "annotation"
    val annSets : List[AnnotationSetExt] = jsAnnSets.children.map(_.extract[AnnotationSetExt])

    var annSetIdsMap : Map[Long, Long] = Map()
    var annSetsMap : Map[AnnotationSetExt, AnnotationSet] = Map()

    //1rst pass - insert Annotation Sets (and generate their internal id)
    annSets.foreach(annSetExt =>  {

        val annSet = AnnotationSet(
          doc.id,
          if (annSetExt.`type` == UserAnnotation) userId else ownerId,
          campaign.id,
          write(annSetExt.text_annotations),
          write(annSetExt.groups),
          write(annSetExt.relations),
          annSetExt.head,
          annSetExt.revision,
          AnnotationSetType.withName(annSetExt.`type`),
          annSetExt.description,
          new Timestamp( inputDateFormat.parse(annSetExt.timestamp).getTime() ) )

        annotation_sets.insert(annSet)
        //store parsed external AnnotationSet and the corresponding AnnotationSet to be able to fixe Annotation references
        annSetsMap  += annSetExt -> annSet
      })

    //List of all Annotations (id is globally unique)
    var allAnnotationIds = ListBuffer[String]()
    //List of the referenced Annotation
    var referencedAnnotations = ListBuffer[AnnotationReference]()

    //2nd pass - fix Annotation references by translating Annotation Set Ids
    annSetsMap.foreach( { case(annSetExt, annSet) =>
          {
            annSetExt.text_annotations.foreach(txt => {
                allAnnotationIds += txt.id }
            )
            annSetExt.groups.foreach(grp => {
                allAnnotationIds += grp.id
                grp.group.foreach(
                  { annRef =>  {
                      rewriteAnnotationRef(annRef, annSetExt.id, annSetIdsMap)
                      referencedAnnotations += annRef
                    }
                  }
                )
              }
            )
            annSetExt.relations.foreach(rel => {
                allAnnotationIds += rel.id
                rel.relation.foreach(
                  { case(role, annRef) =>  {
                        rewriteAnnotationRef(annRef, annSetExt.id, annSetIdsMap)
                        referencedAnnotations += annRef
                      }
                  }
                )
              }
            )

            annSet.groups = write(annSetExt.groups)
            annSet.relations = write(annSetExt.relations)
            annotation_sets.update(annSet)
          }
      })

    //Display referenced annotation absent from the imported file
    referencedAnnotations.foreach(annRef =>  {
        if (! allAnnotationIds.contains(annRef.ann_id)) {
          Console.err.println("Warning: the referenced annotation (id="+ annRef.ann_id +") is missing from the imported file!")
        }
      })

    Console.println("New Document created (id="+ doc.id +") with "+ annSets.length + " AnnotationSet(s)")
    doc
  }
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  def processTextFile(filepath : String , campaign : Campaign, ownerId : Long, userId : Long, assignees : scala.collection.mutable.Set[Long]) = {
    import net.liftweb.json.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val fileNameParser = """(?:.*/)?([^/]+)(?:\.)(.+)""".r
    val sectionTitleParser = """<(h\d)>(.+)</(h\d)>""".r

    val fileNameParser(shortFileName, extension) = filepath

    var aNum = 0;
    //Formatting annotation
    var formatAnnotations = ListBuffer[TextAnnotation]()
    val sb = new StringBuilder()
    val source = io.Source.fromFile(filepath)
    for(line <- source.getLines()) {

      try {
        val sectionTitleParser(openingtag, sectionTitle, closingtag) = line
        val startPos = sb.length
        sb.append(sectionTitle)
        val endPos = sb.length
        aNum += 1
        formatAnnotations += TextAnnotation("fmt-" + aNum, Map(), List(List(startPos, endPos)), openingtag)

      } catch {
        case ex:MatchError =>
          sb.append(line)
          aNum += 1
          val currLen = sb.length
          formatAnnotations += TextAnnotation("fmt-" + aNum, Map(), List(List(currLen, currLen)),"br")
      }
    }
    source.close()

    //add imported file path to the Document properties
    val properties = Map("filePath" -> List(filepath), "shortName" -> List(shortFileName))

    val doc =  Document(ownerId,
                        write(properties),
                        sb.toString,
                        "", //comment
                        shortFileName)
    documents.insert(doc)

    campaign_documents.insert(CampaignDocument(campaign.id,doc.id, None))

    assignees.foreach( aId => document_assignment.insert(DocumentAssignment(campaign.id, aId, doc.id, Some(now()), None)) )

    val formatAnnSet = AnnotationSet(
      doc.id,
      ownerId,
      campaign.id,
      write(formatAnnotations),
      write(Nil),
      write(Nil),
      true,
      0,
      HtmlAnnotation,
      "HTML formatting",
      new Timestamp( new Date().getTime() ) )

    annotation_sets.insert(formatAnnSet)

    doc
  }



}
