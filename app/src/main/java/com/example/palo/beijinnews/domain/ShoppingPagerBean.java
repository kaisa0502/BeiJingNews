package com.example.palo.beijinnews.domain;

/**
 * Created by palo on 2016/10/27.
 */

import java.util.List;

/**
 * 作者：尚硅谷-杨光福 on 2016/10/26 10:22
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：商城热卖的数据
 */
public class ShoppingPagerBean {

    /**
     * totalCount : 28
     * currentPage : 1
     * totalPage : 3
     * pageSize : 10
     * list : [{"id":1,"name":"联想（Lenovo）拯救者14.0英寸游戏本（i7-4720HQ 4G 1T硬盘 GTX960M 2G独显 FHD IPS屏 背光键盘）黑","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_55c1e8f7N4b99de71.jpg","description":null,"price":5979,"sale":8654},{"id":2,"name":"奥林巴斯（OLYMPUS）E-M10-1442-EZ 微单电电动变焦套机 银色 内置WIFI 翻转触摸屏 EM10复古高雅","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_rBEhWlMFnG0IAAAAAAIqnbSuyAAAAIxLwJ57aQAAiq1705.jpg","description":null,"price":3799,"sale":3020},{"id":3,"name":"Apple iPad mini 2 ME276CH/A （配备 Retina 显示屏 7.9英寸 16G WLAN 机型 深空灰色）","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_548574edNc366ff4a.jpg","description":null,"price":1938,"sale":9138},{"id":4,"name":"华为（HUAWEI）P8max 移动联通双4G 双卡双待 星光银）","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_55ac5bc9N7dc9657c.jpg","description":null,"price":3388,"sale":6631},{"id":5,"name":"联想（Lenovo）7英寸平板电脑 （四核1.3GHz 1G/8G 蓝牙 GPS ） A7-10 (WiFi版 8G存储 黑色) 官方标配","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_5535f890Ndfd89dff.jpg","description":null,"price":499,"sale":5742},{"id":6,"name":"未来人类（Terrans Force）X599 15.6英寸游戏本（E3-1231V3 8G 1TB GTX970M 6G独显 3K高清屏）黑","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_55ec08c8Nd34f91bc.jpg","description":null,"price":9999,"sale":8814},{"id":7,"name":"华硕（ASUS）飞行堡垒FX50JX 15.6英寸游戏笔记本电脑（i5-4200H 4G 7200转500G GTX950M 2G独显 全高清）","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_5604b257Ne3fd1a5e.jpg","description":null,"price":4799,"sale":6844},{"id":8,"name":"得力（deli）33123 至尊金贵系列大型办公家用保管箱 全钢防盗 震动报警 高65CM","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_5456e410N65ff4160.jpg","description":null,"price":698,"sale":7777},{"id":9,"name":"AMD Athlon X4（速龙四核）860K盒装CPU （Socket FM2+/3.7GHz/4M/95W）","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_5411486aN1d4ddc5d.jpg","description":null,"price":29,"sale":8355},{"id":10,"name":"金士顿（Kingston）DTM30R 16GB USB3.0 精致炫薄金属U盘","imgUrl":"http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_54b78bf0N24c00fc2.jpg","description":null,"price":42.9,"sale":8442}]
     */

    private int totalCount;
    private int currentPage;
    private int totalPage;
    private int pageSize;
    /**
     * id : 1
     * name : 联想（Lenovo）拯救者14.0英寸游戏本（i7-4720HQ 4G 1T硬盘 GTX960M 2G独显 FHD IPS屏 背光键盘）黑
     * imgUrl : http://7mno4h.com2.z0.glb.qiniucdn.com/s_recommend_55c1e8f7N4b99de71.jpg
     * description : null
     * price : 5979
     * sale : 8654
     */

    private List<Wares> list;

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setList(List<Wares> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<Wares> getList() {
        return list;
    }

    public static class Wares {
        private int id;
        private String name;
        private String imgUrl;
        private String description;
        private float price;
        private int sale;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public void setSale(int sale) {
            this.sale = sale;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getDescription() {
            return description;
        }

        public float getPrice() {
            return price;
        }

        public int getSale() {
            return sale;
        }
    }
}
