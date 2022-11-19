package com.sideproject.codemoim.config;

import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
//import org.apache.lucene.analysis.ko.KoreanFilterFactory;
import org.apache.lucene.analysis.ko.KoreanNumberFilterFactory;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilterFactory;
import org.apache.lucene.analysis.ko.KoreanReadingFormFilterFactory;
import org.apache.lucene.analysis.ko.KoreanTokenizerFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class CustomLuceneAnalysisConfigurer implements LuceneAnalysisConfigurer {

    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer("koreanAnalyzer").custom()
                .tokenizer(KoreanTokenizerFactory.class)
                .charFilter(HTMLStripCharFilterFactory.class)
                .tokenFilter(KoreanReadingFormFilterFactory.class)
                .tokenFilter(KoreanPartOfSpeechStopFilterFactory.class)
                .tokenFilter(KoreanNumberFilterFactory.class)
                //.tokenFilter(KoreanFilterFactory.class)
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(ASCIIFoldingFilterFactory.class);

        context.analyzer("specialWordsAnalyzer").custom()
                .tokenizer(WhitespaceTokenizerFactory.class)
                .charFilter(HTMLStripCharFilterFactory.class)
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(ASCIIFoldingFilterFactory.class);
    }

}
