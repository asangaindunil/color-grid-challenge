import java.lang.Math;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

class nodeDetail {
    private int x;
    private int y;

    public nodeDetail(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    

    // get adjacent nodes
    
    public nodeDetail topNode() {
        return new nodeDetail(x, y-1);
    }

    public nodeDetail bottomNode() {
        return new nodeDetail(x, y+1);
    }

    public nodeDetail leftNode() {
        return new nodeDetail(x-1, y);
    }

    public nodeDetail rightNode() {
        return new nodeDetail(x+1, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        nodeDetail relavent = (nodeDetail) o;

        if (x != relavent.x) return false;
        return y == relavent.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int hashCode() {
        int result = x;
        result = result + y;
        return result;
    }
}

class Node {
    private nodeDetail relavent;
    private char color;
    private int id;

    public Node(int id, int x, int y, char color) {
        this.color = color;
        this.relavent = new nodeDetail(x, y);
        this.id = id;
    }

    public nodeDetail getCoordinate() {
        return relavent;
    }

    public char getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (color != node.color) return false;
        return relavent.equals(node.relavent);
    }
    
    @Override
    public int hashCode() {
        int result = relavent.hashCode();
        result = result + (int) color;
        return result;
    }


    @Override
    public String toString() {
        return " [" + relavent.toString() + ", " + color + "] ";
    }
}

class Block implements Comparable<Block> {

    private char color;

    private Set<Node> nodes;

    public Block(char color) {
        this.color = color;
        nodes = new HashSet<>();
    }

    public Set<Node> allNodes() {
        return nodes;
    }

    public Set<nodeDetail> allRelavents() {
        return nodes.stream().map(n -> n.getCoordinate()).collect(Collectors.toSet());
    }

    public boolean addNode(Node node) {
        if (node != null && !nodes.contains(node)
                && node.getColor() == this.color) {
            return nodes.add(node);
        }
        return false;
    }

    public boolean hasNode(Node node) {
        if(node == null)
            return false;
        return nodes.stream().anyMatch(n -> n.getId() == node.getId());
    }

    public void display() {
        nodes.forEach(n -> System.out.print(n.toString()));
    }

    public int size() {
        return nodes.size();
    }


    @Override
    public int compareTo(Block o) {
        return o.size() - this.size();
    }
}

public class ChallengeTest {

    public static final char[] colorVarients = {'R', 'B', 'W', 'G', 'Y', 'P' }; // R = Red , B = Black, W = White, G = Green, Y = Yellow, P = Pink

    private HashMap<nodeDetail, Node> colorGrid;

    private int col;
    private int row;
    private int colorCount;

    public void createGame(int cols, int rows, int colorCount) {
        this.col = cols;
        this.row = rows;
        this.colorCount = colorCount;

        Random random = new Random();
        this.colorGrid = new HashMap<>();
        for (int i = 0; i < cols*rows ; i++ ) {
            int x = i % cols;
            int y = (int) Math.floor(i/cols);
            this.colorGrid.put(new nodeDetail(x, y), new Node(i, x, y, colorVarients[random.nextInt(3)]));
        }
    }

    public Node getNode(int x, int y) {
        return this.colorGrid.get(new nodeDetail(x, y));
    }

    public void printGame() {
        for (int y = 0; y < row; y++) {
            for(int x = 0; x < col; x++) {
                if(x == this.col - 1 ) {
                    System.out.println(getNode(x, y).getColor());
                } else {
                    System.out.print(getNode(x, y).getColor() + ", ");
                }
            }
        }
    }

    public void finalResult(Block block) {
        for (int y = 0; y < row; y++) {
            for(int x = 0; x < col; x++) {
                Node n = getNode(x, y);
                char color = block.hasNode(n) ? '1' : '0';
                if(x == this.col - 1 ) {
                    System.out.println(color);
                } else {
                    System.out.print(color + ", ");
                }
            }
        }
    }

    private List<Node> findNeighourNodes(Node n, Block block) {
        List<Node> nodes = new ArrayList<>();
        nodeDetail relavent = n.getCoordinate();
        Node topNode = this.colorGrid.get(relavent.topNode());
        if (topNode != null && topNode.getColor() == n.getColor() && !block.hasNode(topNode)) {
            nodes.add(topNode);
        }
        Node bottomNode = this.colorGrid.get(relavent.bottomNode());
        if (bottomNode != null && bottomNode.getColor() == n.getColor() && !block.hasNode(bottomNode)) {
            nodes.add(bottomNode);
        }
        Node rightNode = this.colorGrid.get(relavent.rightNode());
        if (rightNode != null && rightNode.getColor() == n.getColor() && !block.hasNode(rightNode)) {
            nodes.add(rightNode);
        }
        Node leftNode = this.colorGrid.get(relavent.leftNode());
        if (leftNode != null && leftNode.getColor() == n.getColor() && !block.hasNode(leftNode)) {
            nodes.add(leftNode);
        }
        return nodes;
    }

    public Block getContinousBlock(int x, int y) {
        nodeDetail startCoord = new nodeDetail(x, y);
        Node startNode = this.colorGrid.get(startCoord);
        Block block = new Block(startNode.getColor());
        block.addNode(startNode);

        LinkedList<Node> nodesToVisit = new LinkedList<>();
        nodesToVisit.addAll(findNeighourNodes(startNode, block));

        while(!nodesToVisit.isEmpty()) {
            Node nextNode = nodesToVisit.remove();
            block.addNode(nextNode);
            nodesToVisit.addAll(findNeighourNodes(nextNode, block));
        }

        return block;
    }

    public Block getLargestBlock() {
        Set<nodeDetail> allRelavents = new HashSet<>(this.colorGrid.keySet());
        List<Block> allBlocks = new ArrayList<>();
        while(!allRelavents.isEmpty()) {
            nodeDetail coord = allRelavents.iterator().next();
            Block newBlock = getContinousBlock(coord.getX(), coord.getY());
            allBlocks.add(newBlock);
            allRelavents.removeAll(newBlock.allRelavents());
        }
        Collections.sort(allBlocks);
        return allBlocks.size() > 0 ? allBlocks.get(0) : null;
    }

    public static void main(String[] args) {
        int width = 10;
        int height = 10;
        int colorCount = 3;

        ChallengeTest challenge = new ChallengeTest();
        challenge.createGame(width, height, colorCount);
        challenge.printGame();

        long startTime = Instant.now().toEpochMilli();
        Block block = challenge.getLargestBlock();
        long endTime = Instant.now().toEpochMilli();

        if (block != null) {
            System.out.println();
            System.out.println();
            System.out.println("Spend Total Time To Find Continous Block" + (endTime - startTime));
            System.out.println();
            
            
            System.out.println("Final Result");
            System.out.println("***largest connecting block replaced with 1 and other nodes replaced with 0");
            challenge.finalResult(block);
        }
    }



}