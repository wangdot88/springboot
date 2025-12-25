package dangod.springboot.service;

import dangod.springboot.entity.TicketCategory;
import dangod.springboot.entity.User;
import dangod.springboot.repository.TicketCategoryRepository;
import dangod.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 初始化用户
        initUsers();
        
        // 初始化工单分类
        initTicketCategories();
    }
    
    private void initUsers() {
        // 创建管理员用户
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFullName("管理员");
            admin.setRoleType(User.RoleType.ADMIN);
            userRepository.save(admin);
        }
        
        // 创建客服用户
        if (!userRepository.existsByUsername("agent1")) {
            User agent1 = new User();
            agent1.setUsername("agent1");
            agent1.setPassword(new BCryptPasswordEncoder().encode("agent123"));
            agent1.setEmail("agent1@example.com");
            agent1.setFullName("客服小王");
            agent1.setRoleType(User.RoleType.SUPPORT_AGENT);
            userRepository.save(agent1);
        }
        
        if (!userRepository.existsByUsername("agent2")) {
            User agent2 = new User();
            agent2.setUsername("agent2");
            agent2.setPassword(new BCryptPasswordEncoder().encode("agent123"));
            agent2.setEmail("agent2@example.com");
            agent2.setFullName("客服小李");
            agent2.setRoleType(User.RoleType.SUPPORT_AGENT);
            userRepository.save(agent2);
        }
        
        // 创建客户用户
        if (!userRepository.existsByUsername("customer1")) {
            User customer1 = new User();
            customer1.setUsername("customer1");
            customer1.setPassword(new BCryptPasswordEncoder().encode("customer123"));
            customer1.setEmail("customer1@example.com");
            customer1.setFullName("张三");
            customer1.setRoleType(User.RoleType.CUSTOMER);
            userRepository.save(customer1);
        }
        
        if (!userRepository.existsByUsername("customer2")) {
            User customer2 = new User();
            customer2.setUsername("customer2");
            customer2.setPassword(new BCryptPasswordEncoder().encode("customer123"));
            customer2.setEmail("customer2@example.com");
            customer2.setFullName("李四");
            customer2.setRoleType(User.RoleType.CUSTOMER);
            userRepository.save(customer2);
        }
    }
    
    private void initTicketCategories() {
        if (ticketCategoryRepository.count() == 0) {
            // 创建工单分类
            TicketCategory category1 = new TicketCategory();
            category1.setName("技术支持");
            category1.setDescription("技术相关问题，如系统故障、功能异常等");
            ticketCategoryRepository.save(category1);
            
            TicketCategory category2 = new TicketCategory();
            category2.setName("账户问题");
            category2.setDescription("账户登录、密码重置、权限问题等");
            ticketCategoryRepository.save(category2);
            
            TicketCategory category3 = new TicketCategory();
            category3.setName("产品咨询");
            category3.setDescription("产品功能咨询、使用指导等");
            ticketCategoryRepository.save(category3);
            
            TicketCategory category4 = new TicketCategory();
            category4.setName("投诉建议");
            category4.setDescription("用户投诉、建议反馈等");
            ticketCategoryRepository.save(category4);
            
            TicketCategory category5 = new TicketCategory();
            category5.setName("订单问题");
            category5.setDescription("订单相关的问题，如支付、退款等");
            ticketCategoryRepository.save(category5);
        }
    }
}