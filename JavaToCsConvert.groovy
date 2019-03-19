/*
 * Groovy de basit io islemlerini kesfedip katki saglayan Samet Burc kardesime tesekkurler
 * Asagida bir folder altindaki tum sub folderlarda bulunan java classlarini belirtilen 
 * destination pathe c sharp dosyasi olarak convert eden groovy scripti bulunmaktadir. (19/03/2019)
 */
import groovy.io.FileType

typeMapping = [
        String              : "string",
        Integer             : "int?",
        int                 : "int",
        Float               : "float?",
        float               : "float",
        Short               : "short?",
        short               : "short",
        Double              : "double?",
        double              : "double",
        Long                : "long?",
        long                : "long",
        BigDecimal          : "decimal?",
        Date                : "DateTime?",
        boolean             : "bool",
        Boolean             : "bool?",
        Timestamp           : "DateTime?"
]

main("C:\\...\\...\\dto")

def main(javaFilesSourcePath){

    def outputDir =  "C:\\Testoutput"
    def dirlist = []
    def filelist = []
    def dir = new File(javaFilesSourcePath)
    dir.eachFileRecurse (FileType.DIRECTORIES) { directory ->
        dirlist << directory
    }
    dir.eachFileRecurse (FileType.FILES) { file ->
        filelist << file
    }
    dirlist.each {
        main(it.getAbsolutePath())
    }
    filelist.each {
        println(it.getAbsolutePath())
        new File(outputDir, it.name.take(it.name.lastIndexOf('.')) + ".cs").withPrintWriter { out -> generate(out, it) }
    }

}

def generate(out , file){
    def fileContents=file.getText('UTF-8')

    def list=[]
    def className

    fileContents.eachLine {
        if(it.contains(' class ') ){
            className=it
        }
    }

    def nameValues=className.split( ' ' )
    def step=0;
    for (int i = 2; i <nameValues.length ; i++) {
        if(nameValues[i] != ''){
            className=nameValues[i]
            break;
        }
    }

    fileContents.eachLine {
        if(it.contains('private') || it.contains('protected')){
            list.add(it)
        }
    }

    out.println('using System;')
    out.println('using System.Collections.Generic;')
    out.println('using System.Runtime.Serialization;')
    out.println('')
    out.println('namespace TESTDTO.DTO')
    out.println('{')
    out.println('    [DataContract]')
    out.println('    [Serializable()]')
    out.println('    public class '+className)
    out.println('    {')

    list.each{
        def values=it.substring(0,it.indexOf(';')).trim().split( ' |\t' )
        def result='public '
        step=0;
        def boolean b= true;
        for (int i = 1; i <values.length ; i++) {
            if(values[i] != ''){
                if(b){
                    result+=getType(values[i])+' '
                    b=false;
                }else{
                    result+=values[i].capitalize()+' '
                    break;
                }
            }
        }
        result+= '{ get; set; }'

        out.println('')
        out.println('       [DataMember]')
        out.println('       '+result)

    }
    out.println('')
    out.println('    }')
    out.println('}')

}

def getType(type) {
    def typeStr = typeMapping.find {  it.key == type }?.value
    if(typeStr==null){
        println(type)
        typeStr=type;
    }
    return typeStr
}
