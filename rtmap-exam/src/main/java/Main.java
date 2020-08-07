import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @description:
 * @author: alextian
 * @create: 2020-08-07 21:05
 **/
public class Main {
    // 经过多次测试,初始大小使用过10000，20000，50000，75000，100000, 76000这个数值得到的结果最佳
    static Supplier<List<Integer>> listSupplier = () -> new ArrayList<>(76000);
    static BinaryOperator<List<Integer>> listCombiner = (leftList, rightList) -> {
        leftList.addAll(rightList);
        return leftList;
    };

    static Supplier<Set<Integer>> setSupplier = () -> new HashSet<>(20000);
    static BinaryOperator<Set<Integer>> setCombiner = (leftSet, rightSet) -> {
        leftSet.addAll(rightSet);
        return leftSet;
    };
    public static void main(String[] args) {
        // 初始化入参
        List<Integer> inputList = new ArrayList<>(500000);
        for (int i = 0; i < 500000; i++) {
            inputList.add(i);
        }

        long start = System.currentTimeMillis();
        fun(inputList);
        long end = System.currentTimeMillis();
        System.out.printf("used time: " + (end - start) + " ms");
    }

    /**
     * @param input List类型
     * @return
     */
    public static List<Integer> fun(List<Integer> input) {
        Random random = new Random();
        Set<Integer> randomIndex = random.ints(100000, 0, 500000) // 生成100000个 [0, 500000)的随机整数
                .parallel() // 并行执行
                .distinct() // 去重, 得到的随机数数量大概率会不足10万
                .boxed()  // 把int装箱成Integer
                .collect(Collector.of(setSupplier, Set::add, setCombiner)); // 把Steam流转化成Set<Integer>类型
        // 补充随机数至10万
        while (randomIndex.size() < 100000) {
            randomIndex.add(random.nextInt(500000));
        }
        return randomIndex.parallelStream() // 并行执行
                .map(index -> input.get(index)) // 用input里对应index位置的整数替换随机数index
                .collect(Collector.of(listSupplier, List::add, listCombiner)); // 把Steam流转化成List<Integer>类型
    }
}
