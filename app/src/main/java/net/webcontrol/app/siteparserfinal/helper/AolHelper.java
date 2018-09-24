package net.webcontrol.app.siteparserfinal.helper;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дима on 12.04.2016.
 */
public class AolHelper {
    TagNode rootNode;

    public AolHelper(URL htmlPage) throws IOException
    {
        System.out.println("Start AolHelper");
        HtmlCleaner cleaner = new HtmlCleaner();
        rootNode = cleaner.clean(htmlPage);
    }

    public List<TagNode> getLinksByClass(String CSSClassname)
    {
        List<TagNode> linkList = new ArrayList<TagNode>();

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("span", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements[i].getAttributeByName("class");
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType !=null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }
        return linkList;
    }
}
