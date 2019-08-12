package org.std.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.std.mongo.model.Article;

import com.mongodb.client.result.UpdateResult;

@Service
public class ArticleServiceImpl {
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public boolean insertArticle(Article article) {
		mongoTemplate.insert(article);
		return true;
	}
	
	public List<Article> getArticles(String uid){
		Query query = Query.query(Criteria.where("uid").is(uid));
//		long count = mongoTemplate.count(query, Article.class);
		Sort sort = new Sort(Sort.Direction.DESC, "time");
		PageRequest pageRequest = PageRequest.of(1, 15, sort);
		List<Article> articles = mongoTemplate.find(query.with(pageRequest), Article.class);
		return articles;
	}
	
	public boolean updateArticles(Article article) {
		Query query = Query.query(Criteria.where("id").is(article.getId()));
		Update update = new Update();
		update.set("time", article.getTime());
		update.set("content", article.getContent());
		
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Article.class);
		
		return true;
	}
	
	
}
