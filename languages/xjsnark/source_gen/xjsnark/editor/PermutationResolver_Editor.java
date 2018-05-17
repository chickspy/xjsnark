package xjsnark.editor;

/*Generated by MPS */

import jetbrains.mps.nodeEditor.DefaultNodeEditor;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.EditorContext;
import org.jetbrains.mps.openapi.model.SNode;
import jetbrains.mps.nodeEditor.cells.EditorCell_Collection;
import jetbrains.mps.nodeEditor.cells.EditorCell_Constant;
import jetbrains.mps.lang.editor.cellProviders.SingleRoleCellProvider;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import org.jetbrains.mps.openapi.language.SContainmentLink;
import jetbrains.mps.nodeEditor.cellMenu.DefaultChildSubstituteInfo;

public class PermutationResolver_Editor extends DefaultNodeEditor {
  public EditorCell createEditorCell(EditorContext editorContext, SNode node) {
    return this.createCollection_uy0pbk_a(editorContext, node);
  }
  private EditorCell createCollection_uy0pbk_a(EditorContext editorContext, SNode node) {
    EditorCell_Collection editorCell = EditorCell_Collection.createHorizontal(editorContext, node);
    editorCell.setCellId("Collection_uy0pbk_a");
    editorCell.setBig(true);
    editorCell.addEditorCell(this.createConstant_uy0pbk_a0(editorContext, node));
    editorCell.addEditorCell(this.createConstant_uy0pbk_b0(editorContext, node));
    editorCell.addEditorCell(this.createRefNode_uy0pbk_c0(editorContext, node));
    editorCell.addEditorCell(this.createConstant_uy0pbk_d0(editorContext, node));
    editorCell.addEditorCell(this.createRefNode_uy0pbk_e0(editorContext, node));
    editorCell.addEditorCell(this.createConstant_uy0pbk_f0(editorContext, node));
    return editorCell;
  }
  private EditorCell createConstant_uy0pbk_a0(EditorContext editorContext, SNode node) {
    EditorCell_Constant editorCell = new EditorCell_Constant(editorContext, node, "resolve_permutation");
    editorCell.setCellId("Constant_uy0pbk_a0");
    editorCell.setDefaultText("");
    return editorCell;
  }
  private EditorCell createConstant_uy0pbk_b0(EditorContext editorContext, SNode node) {
    EditorCell_Constant editorCell = new EditorCell_Constant(editorContext, node, "(");
    editorCell.setCellId("Constant_uy0pbk_b0");
    editorCell.setDefaultText("");
    return editorCell;
  }
  private EditorCell createRefNode_uy0pbk_c0(EditorContext editorContext, SNode node) {
    SingleRoleCellProvider provider = new PermutationResolver_Editor.argArraySingleRoleHandler_uy0pbk_c0(node, MetaAdapterFactory.getContainmentLink(0x688d542e2a3492cL, 0xa31f0e921fd6a8fbL, 0x280cd37f8502b2fcL, 0x280cd37f8502b2fdL, "argArray"), editorContext);
    return provider.createCell();
  }
  private class argArraySingleRoleHandler_uy0pbk_c0 extends SingleRoleCellProvider {
    public argArraySingleRoleHandler_uy0pbk_c0(SNode ownerNode, SContainmentLink containmentLink, EditorContext context) {
      super(ownerNode, containmentLink, context);
    }
    protected EditorCell createChildCell(SNode child) {
      EditorCell editorCell = super.createChildCell(child);
      installCellInfo(child, editorCell);
      return editorCell;
    }
    private void installCellInfo(SNode child, EditorCell editorCell) {
      editorCell.setSubstituteInfo(new DefaultChildSubstituteInfo(myOwnerNode, myContainmentLink.getDeclarationNode(), myEditorContext));
      if (editorCell.getRole() == null) {
        editorCell.setRole("argArray");
      }
    }
    @Override
    protected EditorCell createEmptyCell() {
      EditorCell editorCell = super.createEmptyCell();
      editorCell.setCellId("empty_argArray");
      installCellInfo(null, editorCell);
      return editorCell;
    }
    protected String getNoTargetText() {
      return "<no argArray>";
    }
  }
  private EditorCell createConstant_uy0pbk_d0(EditorContext editorContext, SNode node) {
    EditorCell_Constant editorCell = new EditorCell_Constant(editorContext, node, ",");
    editorCell.setCellId("Constant_uy0pbk_d0");
    editorCell.setDefaultText("");
    return editorCell;
  }
  private EditorCell createRefNode_uy0pbk_e0(EditorContext editorContext, SNode node) {
    SingleRoleCellProvider provider = new PermutationResolver_Editor.idSingleRoleHandler_uy0pbk_e0(node, MetaAdapterFactory.getContainmentLink(0x688d542e2a3492cL, 0xa31f0e921fd6a8fbL, 0x280cd37f8502b2fcL, 0x280cd37f8502b300L, "id"), editorContext);
    return provider.createCell();
  }
  private class idSingleRoleHandler_uy0pbk_e0 extends SingleRoleCellProvider {
    public idSingleRoleHandler_uy0pbk_e0(SNode ownerNode, SContainmentLink containmentLink, EditorContext context) {
      super(ownerNode, containmentLink, context);
    }
    protected EditorCell createChildCell(SNode child) {
      EditorCell editorCell = super.createChildCell(child);
      installCellInfo(child, editorCell);
      return editorCell;
    }
    private void installCellInfo(SNode child, EditorCell editorCell) {
      editorCell.setSubstituteInfo(new DefaultChildSubstituteInfo(myOwnerNode, myContainmentLink.getDeclarationNode(), myEditorContext));
      if (editorCell.getRole() == null) {
        editorCell.setRole("id");
      }
    }
    @Override
    protected EditorCell createEmptyCell() {
      EditorCell editorCell = super.createEmptyCell();
      editorCell.setCellId("empty_id");
      installCellInfo(null, editorCell);
      return editorCell;
    }
    protected String getNoTargetText() {
      return "<no id>";
    }
  }
  private EditorCell createConstant_uy0pbk_f0(EditorContext editorContext, SNode node) {
    EditorCell_Constant editorCell = new EditorCell_Constant(editorContext, node, ");");
    editorCell.setCellId("Constant_uy0pbk_f0");
    editorCell.setDefaultText("");
    return editorCell;
  }
}