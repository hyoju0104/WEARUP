package com.lec.spring.controller;

import com.lec.spring.config.PrincipalBrandDetails;
import com.lec.spring.domain.BrandItemValidator;
import com.lec.spring.domain.Item;
import com.lec.spring.domain.ItemAttachment;
import com.lec.spring.service.ItemAttachmentService;
import com.lec.spring.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/brand")
public class BrandItemController {

    private final ItemService itemService;
    private final ItemAttachmentService itemAttachmentService;

    @Value("${app.upload.path.item}")
    private String uploadDirItem;

    public BrandItemController(ItemService itemService, ItemAttachmentService itemAttachmentService) {
        this.itemService = itemService;
        this.itemAttachmentService = itemAttachmentService;
    }

    // 상품 등록 폼으로 이동
    @GetMapping("/item/write")
    public String write(Model model) {
        model.addAttribute("item", new Item());     // 빈 Item 객체 전달
        return "brand/item/write";
    }

    // 상품 등록 처리
    @PostMapping("/item/write")
    public String writeOk(@ModelAttribute("item") Item item,
                          BindingResult result,
                          @RequestParam("itemImage") MultipartFile itemImage,
                          @AuthenticationPrincipal PrincipalBrandDetails principal,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        // 상품 유효성 검사
        new BrandItemValidator().validate(item, result);

        // 이미지 없을 경우 오류 처리
        if (itemImage == null || itemImage.isEmpty()) {
            model.addAttribute("error_file", "상품 이미지는 필수입니다.");
            result.reject("error_file");
        }

        // 브랜드 정보 및 존재 여부 설정
        item.setBrand(principal.getBrand());
        item.setIsExist(true);

        // 유효성 에러 발생 시 폼으로 복귀
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                model.addAttribute("error_" + error.getField(), error.getDefaultMessage());
            }

            // 선택한 파일명 flash
            if (itemImage != null && !itemImage.isEmpty()) {
                model.addAttribute("selectedFileName", itemImage.getOriginalFilename());
            }

            return "brand/item/write";
        }

        // DB 저장
        itemService.save(item);

        // 파일 저장
        if (itemImage != null && !itemImage.isEmpty()) {
            try {
                Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDirItem);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                String savedName = UUID.randomUUID() + "_" + itemImage.getOriginalFilename();
                File saveFile = uploadPath.resolve(savedName).toFile();
                itemImage.transferTo(saveFile);

                ItemAttachment attach = new ItemAttachment();
                attach.setItemId(item.getId());
                attach.setSourcename(itemImage.getOriginalFilename());
                attach.setFilename(savedName);

                itemAttachmentService.save(attach);

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "파일 저장 실패");
            }
        }

        model.addAttribute("item", item);
        model.addAttribute("result", 1);
        return "brand/item/writeOk";
    }


    @GetMapping("/list")
    public String list(@AuthenticationPrincipal PrincipalBrandDetails principal, Model model) {
        Long brandId = principal.getBrand().getId();
        List<Item> itemList = itemService.findByBrandId(brandId);

        for (Item item : itemList) {
            List<ItemAttachment> attachments = itemAttachmentService.findByItemId(item.getId());
            if (!attachments.isEmpty()) {
                item.setAttachment(attachments.get(0));
            }
        }

        model.addAttribute("itemList", itemList);
        return "brand/list";
    }

    @GetMapping("/item/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Item item = itemService.detail(id);
        List<ItemAttachment> attachments = itemAttachmentService.findByItemId(id);
        ItemAttachment attachment = !attachments.isEmpty() ? attachments.get(0) : null;

        model.addAttribute("item", item);
        model.addAttribute("attachment", attachment);
        return "brand/item/detail";
    }

    @GetMapping("/item/update/{id}")
    public String update(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal PrincipalBrandDetails principal) {

        Item item = itemService.detail(id);
        if (!item.getBrand().getId().equals(principal.getBrand().getId())) {
            return "redirect:/brand/list";
        }

        List<ItemAttachment> attachments = itemAttachmentService.findByItemId(id);
        ItemAttachment attachment = !attachments.isEmpty() ? attachments.get(0) : null;

        model.addAttribute("item", item);
        model.addAttribute("attachment", attachment);
        return "brand/item/update";
    }

    @PostMapping("/item/update")
    public String updateOk(@ModelAttribute Item item,
                           @RequestParam(value = "itemImage", required = false) MultipartFile itemImage,
                           BindingResult result,
                           @AuthenticationPrincipal PrincipalBrandDetails principal,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        new BrandItemValidator().validate(item, result);

        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error ->
                    model.addAttribute("error_" + error.getField(), error.getDefaultMessage())
            );
            item.setBrand(principal.getBrand());
            model.addAttribute("item", item);

            List<ItemAttachment> attachments = itemAttachmentService.findByItemId(item.getId());
            if (!attachments.isEmpty()) {
                model.addAttribute("attachment", attachments.get(0));
            }

            if (itemImage != null && !itemImage.isEmpty()) {
                model.addAttribute("selectedFileName", itemImage.getOriginalFilename());
            }

            return "brand/item/update";
        }

        item.setBrand(principal.getBrand());
        if (item.getIsExist() == null) item.setIsExist(true);
        itemService.update(item);

        if (itemImage != null && !itemImage.isEmpty()) {
            List<ItemAttachment> before = itemAttachmentService.findByItemId(item.getId());
            for (ItemAttachment old : before) {
                File oldFile = Paths.get(System.getProperty("user.dir"), uploadDirItem, old.getFilename()).toFile();
                if (oldFile.exists()) oldFile.delete();
                itemAttachmentService.deleteById(old.getId());
            }

            try {
                Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDirItem);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                String savedName = UUID.randomUUID() + "_" + itemImage.getOriginalFilename();
                File saveFile = uploadPath.resolve(savedName).toFile();
                itemImage.transferTo(saveFile);

                ItemAttachment attach = new ItemAttachment();
                attach.setItemId(item.getId());
                attach.setSourcename(itemImage.getOriginalFilename());
                attach.setFilename(savedName);

                itemAttachmentService.save(attach);

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "파일 저장 실패");
                return "redirect:/brand/item/update/" + item.getId();
            }
        }

        model.addAttribute("result", 1);
        return "brand/item/updateOk";
    }

    @PostMapping("/item/delete")
    public String deleteItem(@RequestParam Long id,
                             @AuthenticationPrincipal PrincipalBrandDetails principal,
                             Model model) {

        Item item = itemService.detail(id);
        if (!item.getBrand().getId().equals(principal.getBrand().getId())) {
            return "redirect:/brand/list";
        }

        itemService.markAsNotExist(id);

        model.addAttribute("result", 1);
        return "brand/item/deleteOk";
    }


}