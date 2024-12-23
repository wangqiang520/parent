package com.cr999.cn.test;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * 观察目标：栈长
 * 来源微信公众号：Java技术栈
 */
@Getter
public class JavaStackObservable extends Observable {

    private String article;

    /**
     * 发表文章
     * @param article
     */
    public void publish(String article){
        // 发表文章
        this.article = article;

        // 改变状态
        this.setChanged();

        // 通知所有观察者
        this.notifyObservers();
    }

}