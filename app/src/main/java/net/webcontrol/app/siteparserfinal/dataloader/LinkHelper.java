package net.webcontrol.app.siteparserfinal.dataloader;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дима on 08.04.2016.
 */
public class LinkHelper {
    TagNode rootNode;

    //Конструктор
    public LinkHelper(URL htmlPage) throws IOException
    {
        System.out.println("Start LinkHelper");
        //Создаём объект HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner();
        //Загружаем html код сайта
        rootNode = cleaner.clean(htmlPage);
    }

    List<TagNode> getLinksByClass(String CSSClassname)
    {
        List<TagNode> linkList = new ArrayList<TagNode>();

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("a", true);
        TagNode linkElements2[] = rootNode.getElementsByName("span",true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements[i].getAttributeByName("class");
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType !=null  && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }
        for (int i = 0; linkElements2 != null && i < linkElements2.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements2[i].getAttributeByName("class");
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements2[i]);
            }
        }
        return linkList;
    }
}
