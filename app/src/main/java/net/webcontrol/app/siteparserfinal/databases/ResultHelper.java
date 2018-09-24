package net.webcontrol.app.siteparserfinal.databases;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дима on 05.04.2016.
 */
public class ResultHelper {
    private TagNode rootNode;

    public ResultHelper(URL htmlPage) throws IOException
    {
        System.out.println("Start ResultHelper");
        //Создаём объект HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner();
        //Загружаем html код сайта
        rootNode = cleaner.clean(htmlPage);
    }

    public List<TagNode> getLinksByClass(String CSSClassname)
    {
        List<TagNode> linkList = new ArrayList<>();

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("div", true);//("a", true);
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
