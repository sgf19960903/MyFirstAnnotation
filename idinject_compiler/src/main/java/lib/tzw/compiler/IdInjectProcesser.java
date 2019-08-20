package lib.tzw.compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import lib.tzw.annotation.BindId;

@AutoService(Processor.class)
public class IdInjectProcesser extends AbstractProcessor {
    Map<Element,ProxyInfo> proxyInfoMap;
    Messager mPrinter;
    Filer mFiler;
    Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mPrinter = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        proxyInfoMap = new HashMap<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annTypeSet = new HashSet<>();
        annTypeSet.add(BindId.class.getCanonicalName());
        return annTypeSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        proxyInfoMap.clear();
        print("enter process method!!!!!!");
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(BindId.class);
        for(Element element : elementSet){
            checkAnnotationValid(element, BindId.class);

            print("cat the variable element!");
            VariableElement variableEle = (VariableElement) element;
            TypeElement enclosingEle = (TypeElement)variableEle.getEnclosingElement();

            if(proxyInfoMap.get(enclosingEle)==null){
                ProxyInfo proxyInfo = new ProxyInfo(elementUtils,enclosingEle);
                proxyInfoMap.put(enclosingEle,proxyInfo);
            }
            BindId bindId = variableEle.getAnnotation(BindId.class);
            int id = bindId.value();
            proxyInfoMap.get(enclosingEle).injectVariables.put(id,variableEle);
        }

        print("create java file!");
        for(Element key : proxyInfoMap.keySet()){
            ProxyInfo proxyInfo = proxyInfoMap.get(key);
            if(proxyInfo!=null){
                try {
                    JavaFileObject jfo = mFiler.createSourceFile(
                            proxyInfo.getProxyClassFullName(),
                            proxyInfo.getTypeElement());
                    Writer writer = jfo.openWriter();
                    writer.write(proxyInfo.generateJavaCode());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        print("create java file success!");
        return true;
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz)
    {
        if (annotatedElement.getKind() != ElementKind.FIELD)
        {
            print( clazz.getSimpleName()+" must be declared on field.");
            return false;
        }
        if (ClassValidator.isPrivate(annotatedElement))
        {
            print(annotatedElement.getSimpleName()+"() must can not be private.");
            return false;
        }

        return true;
    }

    private void print(String pMsg){
        mPrinter.printMessage(Diagnostic.Kind.NOTE,pMsg);
    }


}
