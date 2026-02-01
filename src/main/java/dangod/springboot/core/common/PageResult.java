package dangod.springboot.core.common;

import lombok.Data;
import org.springframework.data.domain.Page;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<T> records;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Integer page, Integer size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
            page.getContent(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Integer page, Integer size) {
        return new PageResult<>(records, total, page, size);
    }
}
