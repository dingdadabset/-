package org.example.spark_sql.test;

import java.util.HashMap;

import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.expressions.Aggregator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;


public class CityMark extends Aggregator<String, BufferString, String> {

        @Override
        public BufferString zero() {
            return new BufferString(0L, new HashMap<String, Long>());
        }

        /**
         * 分区内预聚合
         * @param b map(城市，sum)
         * @param a 当前行表示的城市
         * @return
         */
        @Override
        public BufferString reduce(BufferString b, String a) {
            HashMap<String, Long> hashMap = b.getMap();
            hashMap.put(a,hashMap.getOrDefault(a,0L)+1);
            b.setTotalCount(b.getTotalCount()+1L);
            return b;
        }

        /**
         * 分区间合并
         * @param b1
         * @param b2
         * @return
         */
        @Override
        public BufferString merge(BufferString b1, BufferString b2) {
            b1.setTotalCount(b1.getTotalCount()+b2.getTotalCount());

            HashMap<String, Long> map1 = b1.getMap();
            HashMap<String, Long> map2 = b2.getMap();

            // 将map2中的数据合并到map1中
            map2.forEach(new BiConsumer<String, Long>() {
                @Override
                public void accept(String s, Long aLong) {
                    map1.put(s,aLong+map1.getOrDefault(s,0L));
                }
            });

            return b1;
        }

        /**
         * 转换： map==> {（上海，200），（北京，100），（天津，300）}
         * @param reduction
         * @return
         */
        @Override
        public String finish(BufferString reduction) {
            // 1. 提取count和map
            Long totalCount = reduction.getTotalCount();
            HashMap<String, Long> map = reduction.getMap();

            // 2. 对map中的value（次数）进行排序：使用treeMap
            TreeMap<Long, String> treeMap = new TreeMap<>();

            // 3. 利用treeMap，对map中数据进行排序
            map.forEach(new BiConsumer<String, Long>() {
                @Override
                public void accept(String s, Long aLong) {
                    if(treeMap.containsKey(aLong)){
                        // 4. 如果已经存在当前值
                        treeMap.put(aLong,treeMap.get(aLong)+"_"+s);
                    }else{
                        // 5. 没有当前值
                        treeMap.put(aLong,s);
                    }
                }
            });

            // 6. 创建resultMark列表
            ArrayList<String> resultMark = new ArrayList<>();

            // 7. 定义sum
            Double sum = 0.0;

            // 8. 当前没有更多的城市数据  或者 已经找到两个城市数据   就  停止循环
            while(!(treeMap.size()==0) && resultMark.size() < 2){
                String cities = treeMap.lastEntry().getValue();
                Long counts = treeMap.lastEntry().getKey();
                String[] citiesArr = cities.split("_");
                for (String city : citiesArr) {
                    double rate = counts.doubleValue() * 100 / totalCount;
                    sum += rate;
                    resultMark.add(city+String.format("%.2f",rate)+"%");
                }
                // 9. 添加完成后删除当前key
                treeMap.remove(counts);
            }

            // 10. 拼接其他城市
            if(treeMap.size() > 0){
                resultMark.add("其他"+String.format("%.2f",100-sum)+"%");
            }else {
                resultMark.add("其他"+String.format("%.2f",0.0)+"%");
            }

            // 10. 创建最后拼接的字符串结果cityMark
            StringBuffer cityMark = new StringBuffer();
            for (String s : resultMark) {
                cityMark.append(s).append("、");
            }

            return cityMark.substring(0,cityMark.length()-1);
        }

        @Override
        public Encoder<BufferString> bufferEncoder() {
            return Encoders.javaSerialization(BufferString.class);
        }

        @Override
        public Encoder<String> outputEncoder() {
            return Encoders.STRING();
        }
    }

