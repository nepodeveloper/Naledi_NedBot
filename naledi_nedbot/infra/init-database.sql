-- =============================================
-- NedBot Email Transaction Processing Database
-- Initialization Script
-- =============================================

-- Create EmailTransactions table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'EmailTransactions')
BEGIN
    CREATE TABLE EmailTransactions (
        Id INT IDENTITY(1,1) PRIMARY KEY,
        TransactionID NVARCHAR(100) NOT NULL UNIQUE,
        RequestType NVARCHAR(50) NOT NULL,
        CustomerEmail NVARCHAR(255) NOT NULL,
        Status NVARCHAR(50) NOT NULL,
        ApplicationID NVARCHAR(100) NULL,
        ProcessedDate DATETIME NOT NULL,
        EmailSubject NVARCHAR(500) NULL,
        ApiResponse NVARCHAR(MAX) NULL,
        CreatedAt DATETIME DEFAULT GETDATE() NOT NULL
    );
    
    PRINT 'EmailTransactions table created successfully';
END
ELSE
BEGIN
    PRINT 'EmailTransactions table already exists';
END
GO

-- Create indexes for performance
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_TransactionID' AND object_id = OBJECT_ID('EmailTransactions'))
BEGIN
    CREATE INDEX IX_TransactionID ON EmailTransactions(TransactionID);
    PRINT 'Index IX_TransactionID created';
END
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_CustomerEmail' AND object_id = OBJECT_ID('EmailTransactions'))
BEGIN
    CREATE INDEX IX_CustomerEmail ON EmailTransactions(CustomerEmail);
    PRINT 'Index IX_CustomerEmail created';
END
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_ProcessedDate' AND object_id = OBJECT_ID('EmailTransactions'))
BEGIN
    CREATE INDEX IX_ProcessedDate ON EmailTransactions(ProcessedDate);
    PRINT 'Index IX_ProcessedDate created';
END
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_RequestType' AND object_id = OBJECT_ID('EmailTransactions'))
BEGIN
    CREATE INDEX IX_RequestType ON EmailTransactions(RequestType);
    PRINT 'Index IX_RequestType created';
END
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Status' AND object_id = OBJECT_ID('EmailTransactions'))
BEGIN
    CREATE INDEX IX_Status ON EmailTransactions(Status);
    PRINT 'Index IX_Status created';
END
GO

-- Create views for reporting

-- View: Recent Transactions
IF NOT EXISTS (SELECT * FROM sys.views WHERE name = 'vw_RecentTransactions')
BEGIN
    EXEC('
    CREATE VIEW vw_RecentTransactions AS
    SELECT TOP 100
        TransactionID,
        RequestType,
        CustomerEmail,
        Status,
        ApplicationID,
        ProcessedDate,
        EmailSubject,
        CreatedAt
    FROM EmailTransactions
    ORDER BY ProcessedDate DESC
    ')
    PRINT 'View vw_RecentTransactions created';
END
GO

-- View: Transaction Summary by Type
IF NOT EXISTS (SELECT * FROM sys.views WHERE name = 'vw_TransactionSummaryByType')
BEGIN
    EXEC('
    CREATE VIEW vw_TransactionSummaryByType AS
    SELECT 
        RequestType,
        COUNT(*) as TotalCount,
        SUM(CASE WHEN Status = ''PROCESSED'' OR Status = ''COMPLETED'' THEN 1 ELSE 0 END) as SuccessCount,
        SUM(CASE WHEN Status = ''ERROR'' OR Status = ''FAILED'' THEN 1 ELSE 0 END) as ErrorCount,
        SUM(CASE WHEN Status = ''REQUIRES_MANUAL_REVIEW'' THEN 1 ELSE 0 END) as ManualReviewCount
    FROM EmailTransactions
    GROUP BY RequestType
    ')
    PRINT 'View vw_TransactionSummaryByType created';
END
GO

-- View: Daily Transaction Volume
IF NOT EXISTS (SELECT * FROM sys.views WHERE name = 'vw_DailyTransactionVolume')
BEGIN
    EXEC('
    CREATE VIEW vw_DailyTransactionVolume AS
    SELECT 
        CAST(ProcessedDate AS DATE) as TransactionDate,
        COUNT(*) as TotalTransactions,
        COUNT(DISTINCT CustomerEmail) as UniqueCustomers
    FROM EmailTransactions
    GROUP BY CAST(ProcessedDate AS DATE)
    ')
    PRINT 'View vw_DailyTransactionVolume created';
END
GO

-- Create stored procedures

-- Procedure: Get Transaction Details
IF NOT EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_GetTransactionDetails')
BEGIN
    EXEC('
    CREATE PROCEDURE sp_GetTransactionDetails
        @TransactionID NVARCHAR(100)
    AS
    BEGIN
        SET NOCOUNT ON;
        
        SELECT 
            TransactionID,
            RequestType,
            CustomerEmail,
            Status,
            ApplicationID,
            ProcessedDate,
            EmailSubject,
            ApiResponse,
            CreatedAt
        FROM EmailTransactions
        WHERE TransactionID = @TransactionID;
    END
    ')
    PRINT 'Stored procedure sp_GetTransactionDetails created';
END
GO

-- Procedure: Get Customer Transaction History
IF NOT EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_GetCustomerTransactionHistory')
BEGIN
    EXEC('
    CREATE PROCEDURE sp_GetCustomerTransactionHistory
        @CustomerEmail NVARCHAR(255),
        @TopN INT = 10
    AS
    BEGIN
        SET NOCOUNT ON;
        
        SELECT TOP (@TopN)
            TransactionID,
            RequestType,
            Status,
            ApplicationID,
            ProcessedDate,
            EmailSubject
        FROM EmailTransactions
        WHERE CustomerEmail = @CustomerEmail
        ORDER BY ProcessedDate DESC;
    END
    ')
    PRINT 'Stored procedure sp_GetCustomerTransactionHistory created';
END
GO

-- Procedure: Clean Old Transactions (retention policy)
IF NOT EXISTS (SELECT * FROM sys.procedures WHERE name = 'sp_CleanOldTransactions')
BEGIN
    EXEC('
    CREATE PROCEDURE sp_CleanOldTransactions
        @RetentionDays INT = 365
    AS
    BEGIN
        SET NOCOUNT ON;
        
        DECLARE @DeletedCount INT;
        DECLARE @CutoffDate DATETIME = DATEADD(DAY, -@RetentionDays, GETDATE());
        
        DELETE FROM EmailTransactions
        WHERE ProcessedDate < @CutoffDate;
        
        SET @DeletedCount = @@ROWCOUNT;
        
        SELECT @DeletedCount as DeletedRecords, @CutoffDate as CutoffDate;
    END
    ')
    PRINT 'Stored procedure sp_CleanOldTransactions created';
END
GO

-- Sample queries for monitoring
PRINT '
=============================================
Sample Monitoring Queries
=============================================

-- View recent transactions
SELECT * FROM vw_RecentTransactions;

-- View transaction summary by type
SELECT * FROM vw_TransactionSummaryByType;

-- View daily transaction volume
SELECT * FROM vw_DailyTransactionVolume;

-- Get specific transaction details
EXEC sp_GetTransactionDetails @TransactionID = ''your-transaction-id'';

-- Get customer transaction history
EXEC sp_GetCustomerTransactionHistory @CustomerEmail = ''customer@example.com'', @TopN = 10;

-- Check for errors in last 24 hours
SELECT * FROM EmailTransactions
WHERE ProcessedDate >= DATEADD(HOUR, -24, GETDATE())
  AND Status IN (''ERROR'', ''FAILED'')
ORDER BY ProcessedDate DESC;

-- Count transactions by status
SELECT Status, COUNT(*) as Count
FROM EmailTransactions
GROUP BY Status
ORDER BY Count DESC;

=============================================
';

PRINT 'Database initialization completed successfully!';
