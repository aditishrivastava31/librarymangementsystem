export interface book{
    quantity:number,
    bookName:string,
    authors:authors[]
    category:Category

}

export interface Category{
    categoryName:string
}
export interface authors{
    authorName:string
}
export interface authors{
    authorName:string,
    
}

export interface bookdto{
    book_id: number,
    book_title:string,
    quantity: number,
    authors: string[],
    category: string,
    avg_rating:number
}


export interface issuebookdetails{
    issue_id:number,
    issueDate: Date,
    issueEndDate: Date,
    returnDate: Date,
    bookTitle:string,
    authors:string[]
    userName: string
    
}


export interface extension{
    
        issueId: number
        username: string,
        booktitle: string,
        requestExtension: Date,
        issueReturnDate: Date
    
}
export interface reviewadd
{
    star:number, 
    comments:string
}

