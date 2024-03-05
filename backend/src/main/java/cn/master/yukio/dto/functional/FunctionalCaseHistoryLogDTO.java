package cn.master.yukio.dto.functional;

import cn.master.yukio.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 03/05/2024
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionalCaseHistoryLogDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private FunctionalCase functionalCase;

    private FunctionalCaseBlob functionalCaseBlob;

    private List<FunctionalCaseCustomField> customFields;

    private List<FunctionalCaseAttachment> caseAttachments;

    private List<FileAssociation> fileAssociationList;
}
